package com.example.web.service.inter;

import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.dto.order.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    List<OrderResponse> getUserOrders(Long userId);

    OrderResponse getOrder(Long orderId);

}