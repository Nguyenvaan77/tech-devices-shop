package com.example.web.repository;

import com.example.web.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    Optional<EmailLog> findByProviderMessageId(String providerMessageId);
}