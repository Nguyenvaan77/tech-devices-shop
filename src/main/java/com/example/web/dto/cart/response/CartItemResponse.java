package com.example.web.dto.cart.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;
    private Long productItemId;
    private String productCode;
    private Integer quantity;
    private BigDecimal price;

}