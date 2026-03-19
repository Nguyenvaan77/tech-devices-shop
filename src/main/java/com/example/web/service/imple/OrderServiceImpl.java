package com.example.web.service.imple;

import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.dto.order.response.OrderResponse;
import com.example.web.entity.*;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.OrderMapper;
import com.example.web.repository.*;
import com.example.web.service.inter.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null) {
            throw new BadRequestException("Order request cannot be null");
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
            throw new BadRequestException("Payment method is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());

        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BadRequestException("Invalid order ID");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return orderMapper.toResponse(order);
    }
}