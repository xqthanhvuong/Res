package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.TableRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.TableResponse;
import com.manager.restaurant.service.TableService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/table")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TableController {
    TableService tableService;

    @PostMapping("/create")
    public JsonResponse<String> createTable(@RequestBody TableRequest request) {
        tableService.createTable(request);
        return JsonResponse.success("Table created");
    }

    @DeleteMapping("/{idTable}")
    public JsonResponse<String> deleteTable(@PathVariable("idTable") String idTable) {
        tableService.deleteTable(idTable);
        return JsonResponse.success("Table deleted");
    }

    @GetMapping("/get/{idRestaurant}")
    public JsonResponse<List<TableResponse>> getTable(@PathVariable("idRestaurant") String idRestaurant) {
        return JsonResponse.success(tableService.getAllTableOfRestaurant(idRestaurant));
    }

}
