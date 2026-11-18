package com.example.web.dto.oauth2;

import java.util.List;

import lombok.Data;

@Data
public class GoogleAccessTokenData {
    public String accessToken;
    public Integer expiresIn;
    public List<String> scope;
    public String tokenType;
    public String idToken;
}
