package com.example.web.dto.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUrlResponse {
    private String paymentUrl;
}
