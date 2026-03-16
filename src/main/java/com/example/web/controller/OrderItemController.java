package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/items")
public class OrderItemController {

    @GetMapping
    public Object getItems(@PathVariable Long orderId) {
        return ApiResponse.success(null, "Order items retrieved", 200);
    }

    @PostMapping
    public Object createItem(
            @PathVariable Long orderId,
            @RequestBody Object request) {

        return ApiResponse.success(null, "Order item created", 201);
    }

    @DeleteMapping("/{itemId}")
    public Object deleteItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {

        return ApiResponse.success(null, "Order item deleted", 204);
    }
}