package com.example.web.listener;

import com.example.web.event.PaymentSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class PaymentEmailListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("EMAIL_EVENT_RECEIVED - PaymentSuccessEvent for orderId: {}", event.orderId());

        try {
            // Simulate sending email
            String emailContent = String.format(
                    "Xin chào %s,\n\nĐơn hàng %s đã được thanh toán thành công.\n\nTổng tiền: %s\nPhương thức: %s\nThời gian: %s",
                    event.customerName(),
                    event.orderCode(),
                    event.totalAmount(),
                    event.paymentMethod(),
                    event.paymentTime()
            );

            log.info("Sending Email to {}:\n{}", event.email(), emailContent);
            
            // Assume success if no exception is thrown
            log.info("EMAIL_SENT_SUCCESS - orderId: {}", event.orderId());
        } catch (Exception e) {
            log.error("EMAIL_SENT_FAILED - orderId: {}, Error: {}", event.orderId(), e.getMessage());
            // Do not rethrow, let it fail silently without rolling back transaction
        }
    }
}
