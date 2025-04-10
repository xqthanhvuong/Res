package com.manager.restaurant.repository;

import com.manager.restaurant.dto.response.Bill.FoodDetails;
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


    Bill findByTable_IdTableAndStatus(String idTable, String status);

    @Query("select new com.manager.restaurant.dto.response.Bill.FoodDetails(f.idFood, f.name, f.price, f.image, o.quantity, o.idOrder, o.status, o.payment)" +
            "from Food f join Order o on f.idFood = o.food.idFood " +
            "where o.bill.idBill = :idBill")
    List<FoodDetails> getFoodDetails(@Param("idBill") String idBill);

    @Query("select new com.manager.restaurant.dto.response.Bill.FoodDetails(f.idFood, f.name, f.price, f.image, o.quantity, o.idOrder, o.status, o.payment)" +
            "from Food f join Order o on f.idFood = o.food.idFood " +
            "where o.bill.idBill = :idBill and (o.status <> :status)")
    List<FoodDetails> getFoodDetailsDontHaveStatus(@Param("idBill") String idBill, @Param("status") String status);

    @Query("select b from Bill b where b.table.restaurant.idRestaurant = :idRestaurant " +
            "and b.status = :status")
    List<Bill> findByRestaurantIdAndStatus(@Param("idRestaurant") String idRestaurant, @Param("status") String status);
}
