package com.example.web.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.web.config.SendGridProperties;
import com.example.web.event.PaymentSuccessEvent;
import com.example.web.util.PaymentEmailTemplate;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendGridEmailService implements EmailService {

    private final SendGridProperties sendGridProperties;

    @Override
    public String sendPaymentSuccessEmail(PaymentSuccessEvent event) throws Exception {
        if (sendGridProperties.getApiKey() == null || sendGridProperties.getApiKey().isEmpty()) {
            log.warn("SendGrid API Key is missing. Cannot send email.");
            throw new RuntimeException("SendGrid API Key is missing");
        }

        Email from = new Email(sendGridProperties.getSenderEmail(), sendGridProperties.getSenderName());
        String subject = "Xác nhận thanh toán đơn hàng #" + event.orderId() + " - Tech Devices Shop";
        Email to = new Email(event.email(), event.customerName() != null ? event.customerName() : event.email());
        
        String htmlContent = PaymentEmailTemplate.buildPaymentSuccessHtml(event);
        Content content = new Content("text/html", htmlContent);
        
        Mail mail = new Mail(from, subject, to, content);
        
        SendGrid sg = new SendGrid(sendGridProperties.getApiKey());
        Request request = new Request();
        try {
            log.debug("SENDGRID_REQUEST_SENT: Gửi yêu cầu SendGrid cho đơn hàng {}", event.orderId());
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            log.debug("SENDGRID_RESPONSE_RECEIVED: StatusCode: {}", response.getStatusCode());
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                // Lấy Message-ID từ Headers trả về để tracking webhook
                String messageId = response.getHeaders().get("X-Message-Id");
                if (messageId != null) {
                    return messageId;
                }
                return "unknown-message-id-" + System.currentTimeMillis();
            } else {
                log.error("SENDGRID_API_ERROR: {}. Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Lỗi từ SendGrid API: " + response.getBody());
            }
        } catch (IOException ex) {
            log.error("SENDGRID_IO_EXCEPTION: Lỗi khi gọi SendGrid API", ex);
            throw new Exception("Không thể gửi email: " + ex.getMessage(), ex);
        }
    }
}
