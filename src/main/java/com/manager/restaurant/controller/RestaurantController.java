package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.RestaurantRequest;
import com.manager.restaurant.dto.request.UpdateRestaurantRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.RestaurantResponse;
import com.manager.restaurant.service.RestaurantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantController {
    RestaurantService restaurantService;


    @PostMapping("/create")
    public JsonResponse<String> create(@RequestBody RestaurantRequest request) {
        restaurantService.createRestaurant(request);
        return JsonResponse.success("Create restaurant successfully");
    }

    @PostMapping("/update-name")
    public JsonResponse<String> updateName(@RequestBody UpdateRestaurantRequest request) {
        restaurantService.updateRestaurant(request);
        return JsonResponse.success("Update restaurant successfully");
    }

    @DeleteMapping("/{idRestaurant}")
    public JsonResponse<String> delete(@PathVariable String idRestaurant) {
        restaurantService.delete(idRestaurant);
        return JsonResponse.success("Delete restaurant successfully");
    }

    @GetMapping("/get-my-restaurant")
    public JsonResponse<List<RestaurantResponse>> getMyRestaurant() {
        return JsonResponse.success(restaurantService.getMyRestaurant());
    }


}
