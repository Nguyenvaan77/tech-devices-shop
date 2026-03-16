package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.service.inter.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public Object getCart(@PathVariable Long userId) {

        return ApiResponse.success(
                cartService.getCart(userId)
        );
    }

    @PostMapping("/{userId}/items")
    public Object addToCart(
            @PathVariable Long userId,
            @RequestBody AddToCartRequest request
    ) {

        return ApiResponse.success(
                cartService.addToCart(userId, request)
        );
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public Object removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {

        return ApiResponse.success(
                cartService.removeFromCart(userId, productId)
        );
    }
}