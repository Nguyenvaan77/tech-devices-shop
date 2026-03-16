package com.example.web.dto.address.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private String receiverName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String detail;
}