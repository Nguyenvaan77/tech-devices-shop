package com.example.web.service.imple;

import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.dto.order.response.OrderResponse;
import com.example.web.entity.*;
import com.example.web.mapper.OrderMapper;
import com.example.web.repository.*;
import com.example.web.service.inter.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();

        order.setUser(user);
        order.setStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());
        //order.setTotalPrice(request.getTotalPrice());

        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getUserOrders(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderMapper.toResponse(order);
    }
}