package com.example.web.dto.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private String paymentCode;
    private String gatewayTransactionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String responseCode;
    private String bankCode;
    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long orderId;
}
