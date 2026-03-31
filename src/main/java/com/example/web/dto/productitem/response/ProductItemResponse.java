package com.example.web.dto.productitem.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemResponse {
    private Long id;
    private String productCode;
    private BigDecimal originalPrice;
}
