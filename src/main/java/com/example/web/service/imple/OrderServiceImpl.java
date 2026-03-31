package com.example.web.service.imple;

import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.dto.order.response.OrderResponse;
import com.example.web.dto.order.response.OrderItemResponse;
import com.example.web.entity.*;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.OrderMapper;
import com.example.web.repository.*;
import com.example.web.service.inter.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Order request cannot be null");
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
            throw new BadRequestException("Payment method is required");
        }

        if (request.getAddressId() == null || request.getAddressId() <= 0) {
            throw new BadRequestException("Address is required");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Address not found with id: " + request.getAddressId()));

        Order order = Order.builder()
                .user(user)
                .address(address)
                .status("PENDING")
                .paymentMethod(request.getPaymentMethod())
                .createdAt(LocalDateTime.now())
                .totalPrice(java.math.BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
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

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus("CANCELLED");
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BadRequestException("Invalid order ID");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(orderMapper::toItemResponse)
                .collect(Collectors.toList());
    }
}
