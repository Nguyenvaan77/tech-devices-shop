package com.example.web.service.imple;

import java.util.Optional;

import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.stereotype.Service;

import com.example.web.dto.oauth2.OAuthAccountCreateDto;
import com.example.web.entity.OAuthAccount;
import com.example.web.entity.User;
import com.example.web.repository.OAuthAccountRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.OAuthAccountService;
import com.example.web.service.inter.UserService;
import com.example.web.util.AuthProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthAccountServiceImpl implements OAuthAccountService{
    private final OAuthAccountRepository oAuthAccountRepository;
    private final UserRepository userRepository;

    @Override
    public OAuthAccount getAuthAccountByProviderAndSub(AuthProvider provider, String sub) {
        return oAuthAccountRepository.findByProviderAndProviderUserId(provider.name().toLowerCase(), sub).orElseThrow(() -> new RuntimeException("This OAuthAccount is not found"));
    }

    @Override
    public Boolean existOAuthAccountByProviderAndSub(String provider, String sub) {
        return oAuthAccountRepository.existsByProviderAndProviderUserId(provider, sub);
    }

    @Override
    public OAuthAccount createOAuthAccount(OAuthAccountCreateDto dto) {
        if(oAuthAccountRepository.existsByProviderAndProviderUserId(dto.getProvider(), dto.getProviderUserId())) {
            throw new RuntimeException("This OAuthAccount is not found");
        }

        User user = userRepository.findById(dto.getUser_id()).orElseThrow(() -> new RuntimeException("This user is not found"));

        OAuthAccount oAuthAccount = new OAuthAccount();
        oAuthAccount.setUser(user);
        oAuthAccount.setProvider(dto.getProvider());
        oAuthAccount.setProviderUserId(dto.getProviderUserId());
        oAuthAccount.setEmail(dto.getEmail());
        oAuthAccount.setAccessToken(dto.getAccessToken());
        oAuthAccount.setExpiresAt(null);

        oAuthAccountRepository.save(oAuthAccount);
        return oAuthAccount;
    }

    
    
}
