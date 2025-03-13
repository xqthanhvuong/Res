package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Payment;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

        // Find payment by account id.
        @Query("SELECT p FROM Payment p WHERE p.restaurant.id = (SELECT a.restaurant.id FROM Account a WHERE a.id = :accountId)")
        Optional<Payment> findByAccountId(@Param("accountId") String accountId);

        // Find payment by id.
        Optional<Payment> findByIdPayment(String idPayment);

        // Update payment details by payment id.
        @Transactional
        @Modifying
        @Query("UPDATE Payment p SET p.restaurant.id = :restaurantId, p.partnerCode = :partnerCode, " +
                        "p.accessKey = :accessKey, p.secretKey = :secretKey WHERE p.idPayment = :paymentId")
        int updatePayment(@Param("paymentId") String paymentId,
                        @Param("restaurantId") String restaurantId,
                        @Param("partnerCode") String partnerCode,
                        @Param("accessKey") String accessKey,
                        @Param("secretKey") String secretKey);

}
