package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.FoodRequest;
import com.manager.restaurant.dto.response.FoodResponse;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.service.FoodService;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodController {
    FoodService foodService;

    @PostMapping("/create")
    public JsonResponse<String> addFood(@RequestBody FoodRequest request){
        return JsonResponse.success(foodService.addFood(request));
    }

    @GetMapping("/get/{idMenu}")
    public JsonResponse<List<FoodResponse>> getFoods(@PathVariable("idMenu") String idMenu){
        return JsonResponse.success(foodService.getFoods(idMenu));
    }

    @PostMapping("/update")
    public JsonResponse<String> updateFood(@RequestBody FoodRequest request) {
        return JsonResponse.success(foodService.updateFood(request));
    }

    @GetMapping("/delete/{idFood}")
    public JsonResponse<String> deleteFood(@PathVariable("idFood") String idFood) {
        return JsonResponse.success(foodService.deleteFood(idFood));
    }

    @GetMapping("/delete-all/{idMenu}")
    public JsonResponse<String> deleteAllFoods(@PathVariable("idMenu") String idMenu) {
        return JsonResponse.success(foodService.deleteAllFoods(idMenu));
    }

    @GetMapping("/get-by-id/{idFood}")
    public JsonResponse<FoodResponse> getFoodById(@PathVariable("idFood") String idFood) {
        return JsonResponse.success(foodService.getFoodById(idFood));
    }

    @GetMapping("/get-by-id-table/{idTable}")
    public JsonResponse<List<FoodResponse>> getFoodByIdTable(@PathVariable("idTable") String idTable){
        return JsonResponse.success(foodService.getFoodByIdTable(idTable));
    }

    @GetMapping("/get-by-id-table-client/{idTable}")
    public JsonResponse<List<FoodResponse>> getFoodByIdTableClient(@PathVariable("idTable") String idTable){
        return JsonResponse.success(foodService.getFoodByIdTableForClient(idTable));
    }
}
