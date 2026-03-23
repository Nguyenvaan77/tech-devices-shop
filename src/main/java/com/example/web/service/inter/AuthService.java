package com.example.web.service.inter;

import org.springframework.stereotype.Service;

import com.example.web.dto.TokenResponse;
import com.example.web.dto.auth.LoginRequest;

public interface AuthService {
    TokenResponse authenticate(LoginRequest loginRequest);
}