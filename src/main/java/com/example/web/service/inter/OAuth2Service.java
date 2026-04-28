package com.example.web.service.inter;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.web.dto.oauth2.OAuth2UserInfo;
import com.example.web.util.AuthProvider;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;

public interface OAuth2Service {
    public String getAuthorizationUrl(AuthProvider provider);
    public String handleCallback(AuthProvider provider, String code);
    public String getTokenUrl(AuthProvider provider);
    public Map<String,  String> getDataBodyForTokenUrl(AuthProvider provider, String code);
    public Map<String, Object> fetchToken(AuthProvider provider, String code);
    public OAuth2UserInfo verifyIdToken(AuthProvider provider, String idToken);

    
}