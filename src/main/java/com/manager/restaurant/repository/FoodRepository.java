package com.manager.restaurant.repository;

import com.manager.restaurant.dto.response.FoodResponse;
import com.manager.restaurant.entity.Food;
import com.manager.restaurant.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {
    @Query("""
        SELECT new com.manager.restaurant.dto.response.FoodResponse(f.idFood, f.name, f.price, f.image)
        FROM Food f
        WHERE f.menu.idMenu = :idMenu
            AND EXISTS (SELECT 1 FROM Menu m WHERE m.idMenu = :idMenu AND m.status = 'active')
    """)
    Optional<List<FoodResponse>> getFoods(@Param("idMenu") String idMenu);

    void deleteAllByMenu(Menu menu);

    @Query("""
        SELECT o.food FROM Bill b JOIN b.orders o WHERE b.table.idTable = :idTable AND o.status = 'Open'
    """)
    Optional<List<Food>> getFoodByIdTable(@Param("idTable") String idTable);
}