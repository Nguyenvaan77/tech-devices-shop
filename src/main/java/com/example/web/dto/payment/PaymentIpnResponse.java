package com.example.web.dto.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIpnResponse {
    private String RspCode;
    private String Message;
}
