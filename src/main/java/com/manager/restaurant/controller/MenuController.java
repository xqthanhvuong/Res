package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.MenuRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.MenuResponse;
import com.manager.restaurant.service.MenuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuController {
    MenuService menuService;

    @PostMapping("/create")
    JsonResponse<String> addMenu(@RequestBody MenuRequest request) {
        return JsonResponse.success(menuService.addMenu(request));
    }

    @GetMapping("/get/{idRestaurant}")
    JsonResponse<List<MenuResponse>> getMenu(@PathVariable("idRestaurant") String idRestaurant) {
        return JsonResponse.success(menuService.getMenu(idRestaurant));
    }

    @DeleteMapping("/delete/{idMenu}")
    JsonResponse<String> deleteMenu(@PathVariable("idMenu") String idMenu){
        return JsonResponse.success(menuService.deleteMenu(idMenu));
    }

    @PostMapping("/update-name")
    JsonResponse<String> updateMenu(@RequestBody MenuRequest request) {
        return JsonResponse.success(menuService.updateMenu(request));
    }
}
