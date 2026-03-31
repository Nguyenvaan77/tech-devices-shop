package com.example.web.dto.productitem.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductItemRequest {
    private String productCode;
    private BigDecimal originalPrice;
}
