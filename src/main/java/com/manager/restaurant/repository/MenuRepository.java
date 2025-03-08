package com.manager.restaurant.repository;

import com.manager.restaurant.dto.response.MenuResponse;
import com.manager.restaurant.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {
    @Query("""
        SELECT new com.manager.restaurant.dto.response.MenuResponse(m.idMenu, m.name, m.status) FROM Menu m
        WHERE m.restaurant.idRestaurant = :idRestaurant
    """)
    Optional<List<MenuResponse>> getMenuByRestaurantId(@Param("idRestaurant") String idRestaurant);
}
