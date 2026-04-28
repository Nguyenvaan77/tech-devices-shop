package com.example.web.dto.oauth2;

import org.apache.commons.compress.archivers.StreamingNotSupportedException;

import com.example.web.util.AuthProvider;

import lombok.Data;

@Data
public class OAuth2LoginRequest {
    private AuthProvider provider;
    private String idToken;
}
