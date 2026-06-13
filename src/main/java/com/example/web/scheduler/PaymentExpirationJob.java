package com.example.web.scheduler;

import com.example.web.entity.Order;
import com.example.web.entity.Payment;
import com.example.web.repository.OrderRepository;
import com.example.web.repository.PaymentRepository;
import com.example.web.util.OrderStatus;
import com.example.web.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentExpirationJob {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void expirePendingPayments() {
        log.info("Running PaymentExpirationJob");
        
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(15);
        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT.name(), timeLimit);

        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED.name());
            orderRepository.save(order);

            paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
                if (PaymentStatus.PENDING.name().equals(payment.getStatus())) {
                    payment.setStatus(PaymentStatus.EXPIRED.name());
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                }
            });
            
            log.info("Expired payment for order: {}", order.getId());
        }
    }
}
