package com.manager.restaurant.repository;

import com.manager.restaurant.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, String> {
    List<RestaurantTable> findByRestaurant_IdRestaurant(String restaurantId);
}
