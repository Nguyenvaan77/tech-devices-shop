package com.example.web.repository;

import com.example.web.entity.Payment;
import com.example.web.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from Payment p
        where p.order.id = :orderId
    """)
    Optional<Payment> findByOrderIdWithLock(Long orderId);

    Optional<Payment> findByPaymentCode(String paymentCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.paymentCode = :paymentCode")
    Optional<Payment> findByPaymentCodeWithLock(@Param("paymentCode") String paymentCode);
}