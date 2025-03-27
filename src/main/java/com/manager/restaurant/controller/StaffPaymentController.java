package com.manager.restaurant.controller;

import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.StaffPaymentResponse;
import com.manager.restaurant.service.StaffPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff-payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffPaymentController {
    StaffPaymentService staffPaymentService;

    @GetMapping("/get-salary/{staffUsername}/{month}/{year}")
    public JsonResponse<StaffPaymentResponse> getStaffSalary(
            @PathVariable("staffUsername") String staffUsername,
            @PathVariable("month") int month,
            @PathVariable("year") int year
            )
    {
        return JsonResponse.success(staffPaymentService.getStaffSalary(staffUsername, month, year));
    }

    @GetMapping("/get-all-salary/{idRestaurant}/{month}/{year}")
    public JsonResponse<List<StaffPaymentResponse>> getAllStaffPayments(
            @PathVariable("idRestaurant") String idRestaurant,
            @PathVariable("month") int month,
            @PathVariable("year") int year
    ) {
        return JsonResponse.success(staffPaymentService.getAllStaffPayments(idRestaurant,month,year));
    }
}
