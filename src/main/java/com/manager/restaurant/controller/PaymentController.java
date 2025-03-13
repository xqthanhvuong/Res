package com.manager.restaurant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manager.restaurant.dto.request.PaymentRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.PaymentResponse;
import com.manager.restaurant.service.PaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    // Inject Payment Service
    PaymentService paymentService;

    // API to create payment
    @PostMapping("/create-payment")
    public JsonResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);
        return JsonResponse.success("Create payment successfully.", paymentResponse);
    }

    // API to get payment
    @GetMapping("/get/{accountId}")
    public JsonResponse<PaymentResponse> getPayment(@PathVariable String accountId) {
        PaymentResponse paymentResponse = paymentService.getPayment(accountId);
        return JsonResponse.success("Get payment successfully.", paymentResponse);
    }

    // API to update payment
    @PostMapping("/update-payment")
    public JsonResponse<PaymentResponse> updatePayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.updatePayment(paymentRequest);
        return JsonResponse.success("Update payment successfully.", paymentResponse);

    }

}
