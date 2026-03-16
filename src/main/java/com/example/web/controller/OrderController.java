package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.service.inter.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public Object createOrder(
            @PathVariable Long userId,
            @RequestBody CreateOrderRequest request
    ) {

        return ApiResponse.created(
                orderService.createOrder(userId, request)
        );
    }

    @GetMapping("/user/{userId}")
    public Object getUserOrders(@PathVariable Long userId) {

        return ApiResponse.success(
                orderService.getUserOrders(userId)
        );
    }

    @GetMapping("/{id}")
    public Object getOrder(@PathVariable Long id) {

        return ApiResponse.success(
                orderService.getOrder(id)
        );
    }
}