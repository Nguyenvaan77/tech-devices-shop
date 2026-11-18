package com.example.web.dto.oauth2;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class OAuth2UserInfo {
    private Map<String, Object> attributes = new HashMap<>();
}
