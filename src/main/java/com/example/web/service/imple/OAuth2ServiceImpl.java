package com.example.web.service.imple;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.web.dto.TokenResponse;
import com.example.web.dto.oauth2.OAuth2UserInfo;
import com.example.web.entity.OAuthAccount;
import com.example.web.service.inter.OAuth2Service;
import com.example.web.util.AuthProvider;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;

import lombok.RequiredArgsConstructor;

import org.springframework.http.*;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service{
    private final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String FACEBOOK_AUTH_URL = "https://www.facebook.com/v13.0/dialog/oauth";
    private final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final String REDIRECT_URI = "http://localhost:8080/oauth/callback";

    @Value("${oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.client.registration.google.scope}")
    private List<String> googleScope;

    private final String GOOGLE_ISSUE = "https://accounts.google.com";

    private final JWTProcessor jwtProcessor;

    
    @Override
    public OAuth2UserInfo verifyIdToken(AuthProvider provider, String idToken) {
        try {
        JWTClaimsSet claims = jwtProcessor.process(idToken, null);

        // ===== VALIDATION =====

        // 1. issuer
        if (!GOOGLE_ISSUE.equals(claims.getIssuer())) {
            throw new RuntimeException("Invalid issuer");
        }

        // 2. audience
        if (!claims.getAudience().contains(CLIENT_ID)) {
            throw new RuntimeException("Invalid audience");
        }

        // 3. expiration
        if (claims.getExpirationTime().before(new Date())) {
            throw new RuntimeException("Token expired");
        }

        // 4. email verified
        Boolean emailVerified = (Boolean) claims.getClaim("email_verified");
        if (emailVerified == null || !emailVerified) {
            throw new RuntimeException("Email not verified");
        }

        // ===== MAP DATA =====
        OAuth2UserInfo userInfo = new OAuth2UserInfo();
        Map<String, Object> attributes = userInfo.getAttributes();
        attributes.put("sub", claims.getSubject());
        attributes.put("email", claims.getStringClaim("email"));
        attributes.put("name", claims.getStringClaim("name"));
        attributes.put("picture", claims.getStringClaim("picture"));

        return userInfo;

        } catch (Exception e) {
            throw new RuntimeException("Invalid id_token", e);
        }
    }

    @Override
    public String getAuthorizationUrl(AuthProvider provider) {
        switch (provider) {
            case AuthProvider.GOOGLE:
                return buildGoogleAuthorizationUrl();
            case AuthProvider.FACEBOOK:
                return buildFacebookAuthorizationUrl();
            case AuthProvider.GITHUB:
                return buildGithubAuthorizationUrl();
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    @Override
    public String handleCallback(AuthProvider provider, String code) {
        switch (provider) {
            case AuthProvider.GOOGLE:
                return buildGoogleCallbackUrl(code);
            case AuthProvider.FACEBOOK:
                return buildFacebookCallbackUrl(code);
            case AuthProvider.GITHUB:
                return buildGithubCallbackUrl(code);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    @Override
    public String getTokenUrl(AuthProvider provider) {
        switch (provider) {
            case AuthProvider.GOOGLE:
                return buildGoogleTokenUrl();
            case AuthProvider.FACEBOOK:
                return buildFacebookTokenUrl();
            case AuthProvider.GITHUB:
                return buildGithubTokenUrl();
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    @Override
    public Map<String, String> getDataBodyForTokenUrl(AuthProvider provider, String code) {
        switch (provider) {
            case AuthProvider.GOOGLE:
                return buildGoogleBodyToGetAccessToken(code);

            case AuthProvider.FACEBOOK:
                return buildFacebookBodyToGetAccessToken(code);
            

            case AuthProvider.GITHUB:
                return buildGithubBodyToGetAccessToken(code);
        
            default:
                throw new IllegalArgumentException("This Provider is not supported");
        }
    }

    @Override
    public Map<String,Object> fetchToken(AuthProvider provider, String code) {
        RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = getTokenUrl(provider);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = (Map<String, String>) getDataBodyForTokenUrl(provider, code);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> responseSend = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        return responseSend.getBody();
    }

    private Map<String, String> buildGoogleBodyToGetAccessToken(String code) {
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("client_id", CLIENT_ID);
        body.put("client_secret", CLIENT_SECRET);
        body.put("redirect_uri", REDIRECT_URI);
        body.put("grant_type", "authorization_code");
        return body;
    }

    private Map<String, String> buildFacebookBodyToGetAccessToken(String code) {
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("client_id", CLIENT_ID);
        body.put("client_secret", CLIENT_SECRET);
        body.put("redirect_uri", REDIRECT_URI);
        body.put("grant_type", "authorization_code");
        return body;
    }

    private Map<String, String> buildGithubBodyToGetAccessToken(String code) {
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("client_id", CLIENT_ID);
        body.put("client_secret", CLIENT_SECRET);
        body.put("redirect_uri", REDIRECT_URI);
        body.put("grant_type", "authorization_code");
        return body;
    }

    private String buildGoogleTokenUrl() {
        return GOOGLE_TOKEN_URL;
    }

    private String buildFacebookTokenUrl() {
        return null;
    }

    private String buildGithubTokenUrl() {
        return null;
    }

    private String buildGoogleCallbackUrl(String code) {
        return REDIRECT_URI + "?code=" + code + "&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";
    }

    private String buildFacebookCallbackUrl(String code) {
        return null;
    }

    private String buildGithubCallbackUrl(String code) {
        return null;
    }

    private String buildGoogleAuthorizationUrl() {
        String scope = String.join("%20", googleScope);
        return GOOGLE_AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&scope=" + scope + "&state=" + AuthProvider.GOOGLE.name().toLowerCase();
    }

    private String buildFacebookAuthorizationUrl() {
        return FACEBOOK_AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code"  + "&state=" + AuthProvider.FACEBOOK.name().toLowerCase();
    }

    private String buildGithubAuthorizationUrl() {
        return GITHUB_AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code"  + "&state=" + AuthProvider.GITHUB.name().toLowerCase();
    }

    
}
