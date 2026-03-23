package com.example.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "email isn't Blank")
    @NotNull(message = "email isn't NULL")
    private String email;

    @NotBlank(message = "password isn't Blank")
    @NotNull(message = "password isn't NULL")
    private String password;
}