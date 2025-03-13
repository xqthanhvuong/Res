package com.manager.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manager.restaurant.entity.OTP;

import jakarta.transaction.Transactional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, String> {

    // Marks the OTP as used if it exists and has not been used before.
    @Transactional
    @Modifying
    @Query("UPDATE OTP o SET o.used = true WHERE o.phone = :phone AND o.otpCode = :otpCode AND o.used = false")
    int markOtpAsUsed(String phone, String otpCode);

    // Check phone is exist
    @Transactional
    boolean existsByPhone(String phone);

    // Delete all OTP code base on phone
    @Transactional
    void deleteByPhone(String phone);

}
