package com.example.web.controller;

import com.example.web.dto.SendGridWebhookEvent;
import com.example.web.entity.EmailLog;
import com.example.web.entity.EmailStatus;
import com.example.web.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class EmailWebhookController {

    private final EmailLogRepository emailLogRepository;

    @PostMapping("/sendgrid")
    public ResponseEntity<Void> handleSendGridWebhook(@RequestBody List<SendGridWebhookEvent> events) {
        log.info("Received SendGrid Webhook with {} events", events.size());

        for (SendGridWebhookEvent event : events) {
            String sgMessageId = event.getSgMessageId();
            if (sgMessageId == null) {
                continue;
            }

            String messageIdToSearch = sgMessageId.split("\\.")[0];

            emailLogRepository.findByProviderMessageId(messageIdToSearch).ifPresentOrElse(emailLog -> {
                updateEmailLogStatus(emailLog, event);
                emailLogRepository.save(emailLog);
            }, () -> {
                log.warn("Không tìm thấy EmailLog với providerMessageId: {}", messageIdToSearch);
            });
        }

        return ResponseEntity.ok().build();
    }

    private void updateEmailLogStatus(EmailLog emailLog, SendGridWebhookEvent event) {
        String eventType = event.getEvent().toLowerCase();
        log.info("Cập nhật trạng thái EmailLog {} thành {}", emailLog.getId(), eventType);

        switch (eventType) {
            case "delivered":
                emailLog.setStatus(EmailStatus.DELIVERED);
                emailLog.setDeliveredAt(LocalDateTime.now());
                break;
            case "bounce":
            case "dropped":
                emailLog.setStatus(EmailStatus.BOUNCED);
                emailLog.setFailureReason(event.getReason());
                break;
            case "deferred":
                emailLog.setStatus(EmailStatus.FAILED);
                emailLog.setFailureReason("Deferred by SendGrid: " + event.getReason());
                break;
            default:
                log.debug("Bỏ qua sự kiện không xử lý: {}", eventType);
                break;
        }
    }
}
