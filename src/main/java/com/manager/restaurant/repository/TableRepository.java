package com.manager.restaurant.repository;

import com.manager.restaurant.dto.response.chart.TableChart;
import com.manager.restaurant.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, String> {
    List<RestaurantTable> findByRestaurant_IdRestaurant(String restaurantId);

    @Query(value = "SELECT t.id_table, t.name_table " +
            "FROM tables t " +
            "LEFT JOIN ( " +
            "    SELECT b.id_table, SUM(o.quantity * (SELECT f.price FROM foods f WHERE f.id_food = o.id_food)) AS total_sales " +
            "    FROM orders o " +
            "    INNER JOIN bills b ON o.id_bill = b.id_bill " +
            "    WHERE b.created_at BETWEEN :lastWeekStart AND :thisWeekEnd " +
            "    GROUP BY b.id_table " +
            ") AS table_sales ON t.id_table = table_sales.id_table " +
            "WHERE t.id_restaurant = :idRestaurant " +
            "ORDER BY COALESCE(table_sales.total_sales, 0) DESC " +
            "LIMIT 3", nativeQuery = true)
    Optional<List<Object[]>> getTableCharts(
            @Param("lastWeekStart") String lastWeekStart,
            @Param("thisWeekEnd") String thisWeekEnd,
            @Param("idRestaurant") String idRestaurant
    );
}
