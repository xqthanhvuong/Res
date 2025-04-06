package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.CreateWorkDayRequest;
import com.manager.restaurant.dto.request.WorkDayRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.WorkDayResponse;
import com.manager.restaurant.service.WorkDayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-day")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkDayController {
    WorkDayService workDayService;

    // Get work day by id
    @GetMapping("/get-by-id/{idWorkDay}")
    public JsonResponse<WorkDayResponse> getById(@PathVariable("idWorkDay") String idWorkDay) {
        return JsonResponse.success(workDayService.getById(idWorkDay));
    }
    // Get work day by date
    @GetMapping("/get-by-date/{date}/{staffId}")
    public JsonResponse<List<WorkDayResponse>> getByDate(
            @PathVariable("date") String dateString,
            @PathVariable("staffId") String staffId
    ) {
        return JsonResponse.success(workDayService.getByDate(dateString, staffId));
    }

    // Get work days form date start to date end
    @GetMapping("/get-by-time-span/{dateStart}/{dateEnd}/{staffId}")
    public JsonResponse<List<WorkDayResponse>> getByTimeSpan(
            @PathVariable("dateStart") String dateStart,
            @PathVariable("dateEnd") String dateEnd,
            @PathVariable("staffId") String staffId
    ) {
        return JsonResponse.success(workDayService.getByTimeSpan(dateStart, dateEnd, staffId));
    }
    // Get work days by month
    @GetMapping("/get-by-month/{month}/{year}/{staffId}")
    public JsonResponse<List<WorkDayResponse>> getByMonth(
            @PathVariable("month") int month,
            @PathVariable("year") int year,
            @PathVariable("staffId") String staffId
    ) {
        return JsonResponse.success(workDayService.getByMonth(month, year, staffId));
    }

    @PostMapping("/create")
    public JsonResponse<String> createWorkDays(@RequestBody CreateWorkDayRequest request){
        return JsonResponse.success(workDayService.createWorkDay(request));
    }

    @PostMapping("/update")
    public JsonResponse<String> updateWorkDay(@RequestBody WorkDayRequest request) {
        return JsonResponse.success(workDayService.updateWorkDay(request));
    }

    @GetMapping("/delete/{idWorkDay}")
    public JsonResponse<String> deleteWorkDay(@PathVariable("idWorkDay") String idWorkDay){
        return JsonResponse.success(workDayService.deleteWorkDay(idWorkDay));
    }
}
