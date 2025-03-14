package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {

    @Query("""
        SELECT
        DAY(b.createdAt) AS transactionDate, SUM(f.price * o.quantity) AS totalPrice
        FROM Bill b
        JOIN b.orders o
        JOIN o.food f
        JOIN f.menu m
        WHERE
        b.status = 'Close' AND
        MONTH(b.createdAt) = :month AND
        YEAR(b.createdAt) = :year AND
        m.restaurant.idRestaurant = :idRestaurant
        GROUP BY DAY(b.createdAt)
        ORDER BY b.createdAt
    """)
    Optional<List<Object[]>> getMonthlyChart(
            @Param("month") int month,
            @Param("year") int year,
            @Param("idRestaurant") String idRestaurant
    );

    @Query(value = "SELECT DAYOFWEEK(DATE(b.created_at)) AS day_of_week, " +
            "SUM(f.price * o.quantity) AS total_sales " +
            "FROM bills b " +
            "JOIN orders o ON b.id_bill = o.id_bill " +
            "JOIN foods f ON o.id_food = f.id_food " +
            "WHERE b.status = 'Close' " +
            "AND b.created_at BETWEEN :start AND :end " +
            "AND b.id_table = :idTable " +
            "GROUP BY DAYOFWEEK(DATE(b.created_at)) " +
            "ORDER BY day_of_week", nativeQuery = true)
    Optional<List<Object[]>> getDateOfWeekSale(
            @Param("start") String lastWeekStart,
            @Param("end") String lastWeekEnd,
            @Param("idTable") String idTable
    );
}
