package com.example.web.listener;

import com.example.web.entity.Notification;
import com.example.web.event.*;
import com.example.web.repository.NotificationRepository;
import com.example.web.util.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("NOTIFICATION_EVENT_RECEIVED - PaymentSuccessEvent for orderId: {}", event.orderId());
        saveNotification(
                event.userId(),
                "Thanh toán thành công",
                String.format("Đơn hàng %s đã được thanh toán thành công.", event.orderCode()),
                NotificationType.PAYMENT_SUCCESS
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("NOTIFICATION_EVENT_RECEIVED - PaymentFailedEvent for orderId: {}", event.orderId());
        saveNotification(
                event.userId(),
                "Thanh toán thất bại",
                String.format("Thanh toán đơn hàng %s thất bại.", event.orderCode()),
                NotificationType.PAYMENT_FAILED
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderExpired(OrderExpiredEvent event) {
        log.info("NOTIFICATION_EVENT_RECEIVED - OrderExpiredEvent for orderId: {}", event.orderId());
        saveNotification(
                event.userId(),
                "Đơn hàng hết hạn",
                String.format("Đơn hàng %s đã bị hủy do quá thời gian thanh toán.", event.orderCode()),
                NotificationType.ORDER_EXPIRED
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInsufficientStock(InsufficientStockEvent event) {
        log.info("NOTIFICATION_EVENT_RECEIVED - InsufficientStockEvent for productId: {}", event.productId());
        saveNotification(
                event.userId(),
                "Không đủ tồn kho",
                "Sản phẩm hiện không đủ số lượng trong kho.",
                NotificationType.INSUFFICIENT_STOCK
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("NOTIFICATION_EVENT_RECEIVED - OrderCancelledEvent for orderId: {}", event.orderId());
        saveNotification(
                event.userId(),
                "Đơn hàng đã bị hủy",
                String.format("Đơn hàng %s đã bị hủy.", event.orderCode()),
                NotificationType.ORDER_CANCELLED
        );
    }

    private void saveNotification(Long userId, String title, String content, NotificationType type) {
        try {
            Notification notification = Notification.builder()
                    .userId(userId)
                    .title(title)
                    .content(content)
                    .type(type)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            log.info("NOTIFICATION_CREATED_SUCCESS - type: {}", type);
        } catch (Exception e) {
            log.error("NOTIFICATION_CREATED_FAILED - type: {}, Error: {}", type, e.getMessage());
        }
    }
}
