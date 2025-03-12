package com.manager.restaurant.repository;

import com.manager.restaurant.entity.StaffPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffPaymentRepository extends JpaRepository<StaffPayment, String> {
}
