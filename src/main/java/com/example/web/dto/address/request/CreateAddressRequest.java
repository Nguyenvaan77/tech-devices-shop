package com.example.web.dto.address.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAddressRequest {

    private String receiverName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String detail;
}