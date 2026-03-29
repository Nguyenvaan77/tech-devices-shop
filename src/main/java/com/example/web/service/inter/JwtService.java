package com.example.web.service.inter;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import com.example.web.entity.User;
import com.example.web.util.TokenEnum;

public interface JwtService {
    String generateAccessToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String generateResetPasswordToken(UserDetails user);

    String extractUsername(String token, TokenEnum tokenEnum);

    Date extractTokenExpired(String token, TokenEnum tokenEnum);

    boolean isValid(String token, TokenEnum tokenEnum, UserDetails userDetails);
};