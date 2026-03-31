package com.example.web.dto.user.response;

import com.example.web.dto.address.response.AddressResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
    private AddressResponse address;
}