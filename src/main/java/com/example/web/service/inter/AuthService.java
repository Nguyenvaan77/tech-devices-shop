package com.example.web.service.inter;

import javax.print.DocFlavor.STRING;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.web.dto.TokenResponse;
import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.reset.ResetPasswordRequest;
import com.example.web.entity.User;
import com.example.web.util.AuthProvider;

import ch.qos.logback.core.subst.Token;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    User getCurrentUser();

    Long getCurrentUserId();

    TokenResponse authenticate(LoginRequest loginRequest);

    TokenResponse authenticateWithOAuth2(UserDetails user);

    TokenResponse refresh(HttpServletRequest request);

    void logout(HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(String secretKey);

    String changePassword(ResetPasswordRequest request);

    public TokenResponse loginWithOAuth(AuthProvider provider, String code);
}