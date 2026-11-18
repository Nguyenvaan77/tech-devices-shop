package com.example.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.dto.TokenResponse;
import com.example.web.mapper.UserMapper;
import com.example.web.repository.OAuthAccountRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AuthService;
import com.example.web.service.inter.JwtService;
import com.example.web.service.inter.OAuth2Service;
import com.example.web.service.inter.OAuthAccountService;
import com.example.web.service.inter.UserService;
import com.example.web.util.AuthProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth")
@Validated
@RequiredArgsConstructor
public class OAuthController {
    private final OAuth2Service oAuth2Service;
    private final AuthService authService;

    @GetMapping("/authorize/{provider}")
    public String authorize(@PathVariable("provider") String provider,
                            HttpServletResponse response) {
        return oAuth2Service.getAuthorizationUrl(AuthProvider.valueOf(provider.toUpperCase()));
    }

    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> callback(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           HttpServletResponse response) {
        
        AuthProvider provider = AuthProvider.valueOf(state.toUpperCase());

        return new ResponseEntity<>(authService.loginWithOAuth(provider, code), HttpStatus.OK);
    }

    
}
