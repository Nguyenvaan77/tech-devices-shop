package com.example.web.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.harmony.pack200.NewAttributeBands.Integral;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.web.dto.TokenResponse;
import com.example.web.dto.auth.RegisterRequest;
import com.example.web.dto.oauth2.OAuth2UserInfo;
import com.example.web.dto.oauth2.OAuthAccountCreateDto;
import com.example.web.entity.OAuthAccount;
import com.example.web.entity.User;
import com.example.web.mapper.UserMapper;
import com.example.web.repository.OAuthAccountRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AuthService;
import com.example.web.service.inter.JwtService;
import com.example.web.service.inter.OAuth2Service;
import com.example.web.service.inter.OAuthAccountService;
import com.example.web.service.inter.UserService;
import com.example.web.util.AuthProvider;

import ch.qos.logback.core.subst.Token;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth")
@Validated
@RequiredArgsConstructor
public class OAuthController {
    private final JwtService jwtService;

    private final OAuthAccountService oAuthAccountService;

    private final OAuth2Service oAuth2Service;

    private final UserService userService;
    private final AuthService authService;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final OAuthAccountRepository oAuthAccountRepository;

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
