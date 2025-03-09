package com.manager.restaurant.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.manager.restaurant.dto.request.OTPRequest;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.service.OTPService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OTPController {

    // Inject OTP Service.
    OTPService otpService;

    // API to send OTP to phone of user
    @PostMapping("/sendOTP")
    public JsonResponse<String> sendOTP(@RequestBody OTPRequest request) {
        otpService.sendOTP(request);
        return JsonResponse.success("Send OTP successfully.");
    }

    // APT to send OTP to phone of user (get phone by email/username of user)
    @PostMapping("/sendOTP-mail")
    public JsonResponse<String> sendOTPMail(@RequestBody OTPRequest request) {
        otpService.sendOTPMail(request);
        return JsonResponse.success("Send OTP successfully.");
    }

    // APT to active account via OTP code
    @PostMapping("/activeOTP")
    public JsonResponse<String> send(@RequestBody OTPRequest request) {
        otpService.activeOTP(request);
        return JsonResponse.success("Active account successfully.");
    }

    // API to validation OTP code
    @PostMapping("/checkOTP")
    public JsonResponse<String> activeOTP(@RequestBody OTPRequest request) {
        otpService.validateAndMarkOTP(request);
        return JsonResponse.success("Check OTP successfully. OTP is valid.");
    }

}
