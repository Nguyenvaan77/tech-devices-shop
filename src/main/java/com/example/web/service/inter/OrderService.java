package com.example.web.service.inter;

import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.dto.order.response.OrderResponse;
import com.example.web.dto.order.response.OrderItemResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrder(Long orderId);

    OrderResponse cancelOrder(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, String status);

    List<OrderItemResponse> getOrderItems(Long orderId);
}
