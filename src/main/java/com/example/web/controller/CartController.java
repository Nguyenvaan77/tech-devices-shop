package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.response.CartResponse;
import com.example.web.service.inter.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

        private final CartService cartService;

        @GetMapping
        public ResponseEntity<ApiResponse<CartResponse>> getCart(@RequestParam Long userId) {
                CartResponse cart = cartService.getCurrentCart(userId);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @PostMapping("/items")
        public ResponseEntity<ApiResponse<CartResponse>> addToCart(
                        @RequestBody AddToCartRequest request) {
                CartResponse cart = cartService.addToCart(request);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @PatchMapping("/items/{id}")
        public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
                        @PathVariable Long id,
                        @RequestBody AddToCartRequest request) {
                CartResponse cart = cartService.updateCartItemQuantity(id, request.getQuantity());
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @DeleteMapping("/items/{id}")
        public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long id) {
                CartResponse cart = cartService.removeFromCart(id);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @DeleteMapping
        public ResponseEntity<ApiResponse<Void>> clearCart() {
                cartService.clearCart();
                return ResponseEntity.noContent().build();
        }
}
