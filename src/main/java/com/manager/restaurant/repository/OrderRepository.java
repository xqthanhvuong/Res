package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Order findByFood_IdFoodAndBill_IdBill(String food_id, String bill_id);
}
