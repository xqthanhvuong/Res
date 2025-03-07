package com.manager.restaurant.controller;

import com.manager.restaurant.dto.response.chart.ChartResponse;
import com.manager.restaurant.dto.response.chart.ChartWeekTableResponse;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.service.ChartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChartController {

    ChartService chartService;

    @GetMapping("/getMonthlyChart/{idRestaurant}/{month}/{year}")
    public JsonResponse<List<ChartResponse>> getMonthlyChart(@PathVariable("idRestaurant") String idRestaurant,
                                                             @PathVariable("month") int month,
                                                             @PathVariable("year") int year) {
        return JsonResponse.success(chartService.getMonthlyChart(idRestaurant, month, year));
    }

    @GetMapping("/getWeekChartTable/{idRestaurant}")
    public JsonResponse<List<ChartWeekTableResponse>> getWeekChartForTable(@PathVariable("idRestaurant") String idRestaurant){
        return JsonResponse.success(chartService.getWeekChartForTable(idRestaurant));
    }

}
