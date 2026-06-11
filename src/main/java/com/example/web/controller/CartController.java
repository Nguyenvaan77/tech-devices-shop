package com.example.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.request.RemoveFromCartRequest;
import com.example.web.service.inter.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

        private final CartService cartService;

        @PreAuthorize("hasRole('CUSTOMER') or hasRole('BUSINESS')")
        @GetMapping
        public ResponseEntity<ApiResponse<CartByCacheResponse>> getCart(@RequestParam Long userId) {
                CartByCacheResponse cart = cartService.getCurrentCart(userId);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @PreAuthorize("hasRole('CUSTOMER') or hasRole('BUSINESS')")
        @PostMapping("/items")
        public ResponseEntity<ApiResponse<CartByCacheResponse>> addToCart(
                        @RequestBody AddToCartRequest request) {
                CartByCacheResponse cart = cartService.addToCart(request);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }


        @PreAuthorize("hasRole('CUSTOMER') or hasRole('BUSINESS')")
        @DeleteMapping("/items/{id}")
        public ResponseEntity<ApiResponse<CartByCacheResponse>> removeItem(@PathVariable RemoveFromCartRequest request) {
                CartByCacheResponse cart = cartService.removeFromCart(request);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @PreAuthorize("hasRole('CUSTOMER') or hasRole('BUSINESS')")
        @DeleteMapping
        public ResponseEntity<ApiResponse<Void>> clearCart() {
                cartService.clearCart();
                return ResponseEntity.noContent().build();
        }
}
