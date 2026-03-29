package com.example.web.service.imple;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web.service.inter.JwtService;
import com.example.web.service.inter.TokenService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import com.example.web.entity.Token;
import com.example.web.entity.User;
import com.example.web.repository.TokenRepository;
import com.example.web.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public Token create(Token token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional
    public void deleteByUser(User user) {
        Token token = tokenRepository.findByUser(user).get();

        if (token != null) {
            user.setToken(null);
            tokenRepository.delete(token);
        }
    }

    @Override
    public Token findByUserId(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User is not found"));

        return tokenRepository.findByUser(user).get();
    }

    @Override
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public Token provideNewAccessAndRefreshTokenForUser(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token token = tokenRepository.findByUserId(user.getId()).orElse(new Token().builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build());

        token = tokenRepository.save(token);

        return token;
    }

}
