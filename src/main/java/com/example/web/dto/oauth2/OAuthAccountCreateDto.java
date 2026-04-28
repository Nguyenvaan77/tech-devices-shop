package com.example.web.dto.oauth2;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class OAuthAccountCreateDto {
    
    private Long user_id;
    private String provider;
    private String providerUserId;
    private String email;
    private String accessToken;
    private LocalDateTime expiresAt;
}
