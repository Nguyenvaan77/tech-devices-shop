package com.example.web.service.inter;

import javax.print.DocFlavor.STRING;

import org.springframework.stereotype.Service;

import com.example.web.dto.TokenResponse;
import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.reset.ResetPasswordRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    TokenResponse authenticate(LoginRequest loginRequest);

    TokenResponse refresh(HttpServletRequest request);

    void logout(HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(String secretKey);

    String changePassword(ResetPasswordRequest request);
}