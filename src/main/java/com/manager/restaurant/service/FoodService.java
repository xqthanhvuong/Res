package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.FoodRequest;
import com.manager.restaurant.dto.response.FoodResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.FoodRepository;
import com.manager.restaurant.repository.MenuRepository;
import com.manager.restaurant.repository.TableRepository;
import com.manager.restaurant.until.SecurityUtils;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class FoodService {

    FoodRepository foodRepository;
    MenuRepository menuRepository;
    AccountRepository accountRepository;
    TableRepository tableRepository;

    private boolean isOwner(Menu menu){
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        return account.getRole().equals(AccountRole.Owner.name()) &&
                menu.getRestaurant().getIdRestaurant().equals(account.getRestaurant().getIdRestaurant());
    }

    private boolean isTableOwner(RestaurantTable table){
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        return account.getRole().equals(AccountRole.Owner.name()) &&
                table.getRestaurant().getIdRestaurant().equals(account.getRestaurant().getIdRestaurant());
    }
    public String addFood(FoodRequest request) {
        Menu menu;
        if(request.getIdMenu() == null || request.getName() == null || request.getImage() == null){
            throw new BadException(ErrorCode.USERNAME_INVALID);
        } else {
            menu = menuRepository.getReferenceById(request.getIdMenu());
            if (menu.getIdMenu() == null) throw new BadException(ErrorCode.USERNAME_INVALID);
        }
        if(!isOwner(menu))
            throw new BadException(ErrorCode.ACCESS_DENIED);
        Food food = Food.builder()
                .menu(menu)
                .name(request.getName())
                .price((float)request.getPrice())
                .image(request.getImage())
                .build();
        foodRepository.save(food);
        return "ok";
    }

    public List<FoodResponse> getFoods(String idMenu) {
        return foodRepository.getFoods(idMenu).orElseThrow();
    }

    public String updateFood(FoodRequest request) {
        Menu menu;
        Food food;
        if(request.getIdFood() == null || request.getIdMenu() == null || request.getName() == null || request.getImage() == null){
            throw new BadException(ErrorCode.USERNAME_INVALID);
        } else {
            menu = menuRepository.getReferenceById(request.getIdMenu());
            food = foodRepository.getReferenceById(request.getIdFood());
            if (menu.getIdMenu() == null || food.getIdFood() == null) throw new BadException(ErrorCode.USERNAME_INVALID);
        }
        if(!isOwner(menu))
            throw new BadException(ErrorCode.ACCESS_DENIED);
        food.setMenu(menu);
        food.setName(request.getName());
        food.setPrice((float) request.getPrice());
        food.setImage(request.getImage());
        foodRepository.save(food);
        return "ok";
    }

    public String deleteFood(String idFood) {
        Food food = foodRepository.getReferenceById(idFood);
        if(food.getMenu() == null || !isOwner(food.getMenu()))
            throw new BadException(ErrorCode.ACCESS_DENIED);
        foodRepository.deleteById(idFood);
        return "ok";
    }

    public String deleteAllFoods(String idMenu) {
        Menu menu = menuRepository.getReferenceById(idMenu);
        if(menu.getIdMenu() == null || !isOwner(menu))
            throw new BadException(ErrorCode.ACCESS_DENIED);
        foodRepository.deleteAllByMenu(menu);
        return "ok";
    }

    public FoodResponse getFoodById(String idFood) {
        var food = foodRepository.getReferenceById(idFood);
        if(food.getIdFood() == null)
            throw new BadException(ErrorCode.NOT_FOND);
        return FoodResponse.builder()
                .idFood(food.getIdFood())
                .name(food.getName())
                .price(food.getPrice())
                .image(food.getImage())
                .build();
    }

    public List<FoodResponse> getFoodByIdTable(String idTable) {
        RestaurantTable table = tableRepository.getReferenceById(idTable);
        if(table.getIdTable() == null || !isTableOwner(table))
            throw new BadException(ErrorCode.ACCESS_DENIED);
        List<FoodResponse> response = new ArrayList<>();
        for(var item : foodRepository.getFoodByIdTable(idTable).orElseThrow(() -> new BadException(ErrorCode.NOT_FOND))){
            response.add(new FoodResponse(item.getIdFood(),item.getName(),item.getPrice(), item.getImage()));
        }
        return response;
    }
}
