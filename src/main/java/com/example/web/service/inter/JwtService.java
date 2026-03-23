package com.example.web.service.inter;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.web.entity.User;

public interface JwtService {  
    String generateToken(UserDetails user);

    
    String extractUsername(String token);

    boolean isValid(String token, UserDetails userDetails);
};