package com.example.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sendgrid")
@Getter
@Setter
public class SendGridProperties {
    private String apiKey;
    private String senderEmail;
    private String senderName;
}