package com.example.web.listener;

import com.example.web.entity.EmailLog;
import com.example.web.entity.EmailStatus;
import com.example.web.event.PaymentSuccessEvent;
import com.example.web.repository.EmailLogRepository;
import com.example.web.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEmailListener {

    private final EmailService emailService;
    private final EmailLogRepository emailLogRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        // log.info("PAYMENT_SUCCESS_EVENT_RECEIVED - cho orderid{}", event.orderId());

        EmailLog emailLog = EmailLog.builder()
                .orderId(event.orderId())
                .recipient(event.email())
                .subject("Xác nhận thanh toán đơn hàng #" + event.orderId() + " - Tech Devices Shop")
                .status(EmailStatus.PENDING)
                .build();
        emailLogRepository.save(emailLog);

        try {
            String messageId = emailService.sendPaymentSuccessEmail(event);
            
            emailLog.setProviderMessageId(messageId);
            emailLog.setStatus(EmailStatus.SENT);
            emailLog.setSentAt(LocalDateTime.now());
            emailLogRepository.save(emailLog);

        } catch (Exception e) {
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setFailureReason(e.getMessage());
            emailLogRepository.save(emailLog);
        }
    }
}
