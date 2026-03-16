package com.example.web.dto.cart.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {

    private Long productId;
    private Integer quantity;

}