package com.example.web.dto.auth.reset;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String secretKey;
    private String password;
    private String confirmPassword;
}
