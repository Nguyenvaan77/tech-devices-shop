package com.example.web.service.imple;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import javax.sound.midi.InvalidMidiDataException;

import org.hibernate.grammars.hql.HqlParser.SecondContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.web.dto.TokenResponse;
import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.reset.ResetPasswordRequest;
import com.example.web.dto.oauth2.OAuth2UserInfo;
import com.example.web.entity.OAuthAccount;
import com.example.web.entity.Token;
import com.example.web.entity.User;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.repository.OAuthAccountRepository;
import com.example.web.repository.TokenRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AuthService;
import com.example.web.service.inter.CustomUserDetailService;
import com.example.web.service.inter.JwtService;
import com.example.web.service.inter.OAuth2Service;
import com.example.web.service.inter.TokenService;
import com.example.web.util.AuthProvider;
import com.example.web.util.TokenEnum;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CustomUserDetailService customUserDetailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final OAuthAccountRepository oAuthAccountRepository;
    private final OAuth2Service oAuth2Service;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    @Transactional
    public TokenResponse loginWithOAuth(AuthProvider provider, String code) {

        // 1. Lấy token từ provider
        Map<String, Object> accessData = oAuth2Service.fetchToken(provider, code);

        String idToken = (String) accessData.get("id_token");
        if (idToken == null) {
            throw new RuntimeException("Missing id_token");
        }

        // 2. Verify token
        OAuth2UserInfo userInfo = oAuth2Service.verifyIdToken(provider, idToken);

        String sub = userInfo.getAttributes().get("sub").toString();
        String email = userInfo.getAttributes().get("email").toString();
        String name = userInfo.getAttributes().get("name").toString();

        // 3. Tìm hoặc tạo user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    return userRepository.save(newUser);
                });

        // 4. Tìm hoặc tạo OAuthAccount
        OAuthAccount account = oAuthAccountRepository
                .findByProviderAndProviderUserId(provider.name().toLowerCase(), sub)
                .orElseGet(() -> {
                    OAuthAccount newAcc = new OAuthAccount();
                    newAcc.setProvider(provider.name().toLowerCase());
                    newAcc.setProviderUserId(sub);
                    newAcc.setUser(user);
                    return newAcc;
                });

        // 5. Update token
        account.setEmail(email);
        account.setAccessToken(accessData.get("access_token").toString());

        Integer expiresIn = (Integer) accessData.get("expires_in");
        account.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));

        oAuthAccountRepository.save(account);

        // 6. Sinh JWT nội bộ
        return authenticateWithOAuth2(user);
    }


    @Override
    public TokenResponse authenticateWithOAuth2(UserDetails user) {
        UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
                user, 
                null, 
                user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = (User) authentication.getPrincipal();
        Long userId = currentUser.getId();

        String accessToken = jwtService.generateAccessToken(currentUser);
        String refreshToken = jwtService.generateRefreshToken(currentUser);

        Token token = tokenRepository.findByUserId(currentUser.getId()).orElse(new Token());

        token.setUser(currentUser);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);

        tokenRepository.save(token);

        return TokenResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .userId(userId)
                .build();
    }

    @Override
    public TokenResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

                
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token token = tokenRepository.findByUserId(user.getId()).orElse(new Token().builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build());

        tokenRepository.save(token);

        return TokenResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .userId(userId)
                .build();
    }

    @Override
    public TokenResponse refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("x-token");

        if (StringUtil.isNullOrEmpty(refreshToken)) {
            throw new RuntimeException("x-token must not be blank");
        }

        String email = jwtService.extractUsername(refreshToken, TokenEnum.REFRESH_TOKEN);

        User user = (User) customUserDetailService.userDetailsService().loadUserByUsername(email);

        if (!jwtService.isValid(refreshToken, TokenEnum.REFRESH_TOKEN, (UserDetails) user)) {
            throw new RuntimeException("Refresh Token is invalid");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        Optional<Token> tokenOptional = tokenRepository.findByUserId(user.getId());

        if (tokenOptional.isEmpty() || !tokenOptional.get().getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("Logout: This refresh token are reject");
        }

        Token token = tokenOptional.get();
        token.setAccessToken(newAccessToken);
        tokenRepository.save(token);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public void logout(HttpServletRequest request) {

        String refreshToken = request.getHeader("x-token");

        if (refreshToken == null || refreshToken.isEmpty() || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh is not found or empty ");
        }

        String email = jwtService.extractUsername(refreshToken, TokenEnum.REFRESH_TOKEN);

        User user = (User) customUserDetailService.userDetailsService().loadUserByUsername(email);

        if (!jwtService.isValid(refreshToken, TokenEnum.REFRESH_TOKEN, user)) {
            throw new RuntimeException("Refresh token is invalid");
        }

        tokenService.deleteByUser(user);
    }

    @Override
    public String forgotPassword(String email) {
        // Check email existing
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by this Email"));

        // Check user active
        if (!user.isEnabled()) {
            throw new RuntimeException("User is not active");
        }

        String resetPasswordToken = jwtService.generateResetPasswordToken(user);

        // Send email confirmlink
        String confirmLink = "http://localhost:8080/auth/reset-password";

        return "Sent!";
    }

    @Override
    public String resetPassword(String secretKey) {
        log.info("-----------  RESET PASSWORD ------------");

        final String email = jwtService.extractUsername(secretKey, TokenEnum.RESET_TOKEN);
        User user = (User) customUserDetailService.userDetailsService().loadUserByUsername(email);

        if (!jwtService.isValid(secretKey, TokenEnum.RESET_TOKEN, user)) {
            throw new RuntimeException("This user is invalid by expired time");
        }

        return "Reset!";
    }

    @Override
    public String changePassword(ResetPasswordRequest request) {
        // Kiểm tra lại token và user có hợp lệ không
        final String email = jwtService.extractUsername(request.getSecretKey(), TokenEnum.RESET_TOKEN);
        User user = (User) customUserDetailService.userDetailsService().loadUserByUsername(email);

        if (!jwtService.isValid(request.getSecretKey(), TokenEnum.RESET_TOKEN, user)) {
            throw new RuntimeException("This user is invalid by expired time");
        }
        // xong phần xác thực

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("ConfirmPassword don't match with password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "Changed!";
    }
}
