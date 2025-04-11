package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.ReportRequest;
import com.manager.restaurant.dto.response.AdvancedReportResponse;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.ReportResponse;
import com.manager.restaurant.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
     ReportService reportService;

     @GetMapping("/get/{idReport}")
     JsonResponse<ReportResponse> getReport(@PathVariable("idReport") String idReport) {
          return JsonResponse.success(reportService.getReport(idReport));
     }

     @PostMapping("/create")
     JsonResponse<String> createReport(@RequestBody ReportRequest request) {
          return JsonResponse.success(reportService.createReport(request));
     }

     @PostMapping("/update")
     JsonResponse<String> updateReport(@RequestBody ReportRequest request) {
          return JsonResponse.success(reportService.updateRepost(request));
     }

     @GetMapping("/delete/{idReport}")
     JsonResponse<String> delete(@PathVariable("idReport") String idReport) {
          return JsonResponse.success(reportService.delete(idReport));
     }

     @GetMapping("/get-by-work-day/{idWorkday}")
     JsonResponse<ReportResponse> getReportByWorkDay(@PathVariable("idWorkday") String idWorkday) {
          return JsonResponse.success(reportService.getReportByWorkday(idWorkday));
     }

     @GetMapping("/get-by-restaurant-id/{idRestaurant}")
     JsonResponse<List<AdvancedReportResponse>> getAllReportByIdRestaurant(@PathVariable("idRestaurant") String idRestaurant){
          return  JsonResponse.success(reportService.getAllReportByIdRestaurant(idRestaurant));
     }
}
