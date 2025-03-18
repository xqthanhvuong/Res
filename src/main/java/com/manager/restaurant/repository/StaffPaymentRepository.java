package com.manager.restaurant.repository;

import com.manager.restaurant.entity.StaffPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffPaymentRepository extends JpaRepository<StaffPayment, String> {
    Optional<StaffPayment> findByAccount_Username(String username);
}
