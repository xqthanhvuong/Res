package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.ReportRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.ReportResponse;
import com.manager.restaurant.service.ReportService;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
     final ReportService reportService;

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
}
