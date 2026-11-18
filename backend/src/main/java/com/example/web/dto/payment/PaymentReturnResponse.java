package com.example.web.dto.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReturnResponse {
    private String status; // e.g. "SUCCESS", "FAILED", "INVALID_SIGNATURE"
    private String message;
    private String paymentCode;
    private String gatewayTransactionId;
    private BigDecimal amount;
    private String bankCode;
}
