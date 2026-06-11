package com.example.web.dto.cart.bycache;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.StandardException;

@Data
@Builder
public class CartByCacheResponse {
    private Long userId;
    List<CartItemByCacheResponse> listCartItems;
}
