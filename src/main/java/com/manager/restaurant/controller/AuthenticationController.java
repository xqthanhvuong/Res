package com.manager.restaurant.controller;

import com.manager.restaurant.dto.request.*;
import com.manager.restaurant.dto.response.AccountResponse;
import com.manager.restaurant.dto.response.AuthenticationResponse;
import com.manager.restaurant.dto.response.IntrospectResponse;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.service.AuthenticationService;
import com.manager.restaurant.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/login")
    public JsonResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest request){
        return JsonResponse.success(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public JsonResponse<String> register(@RequestBody AccountRequest request){
        userService.createAccount(request);
        return JsonResponse.success("Register success");
    }

    @PostMapping("/update")
    public JsonResponse<String> update(@RequestBody AccountUpdateRequest request){
        userService.updateAccount(request);
        return JsonResponse.success("Update success");
    }

    @PostMapping("/create-employee")
    public JsonResponse<String> createStaff(@RequestBody StaffRequest request){
        userService.createStaff(request);
        return JsonResponse.success("Create staff success");
    }

    @PostMapping("/update-device-token")
    public JsonResponse<String> updateDeviceToken(@RequestBody UpdateDeviceRequest request){
        userService.updateDeviceToken(request);
        return JsonResponse.success("Update device token success");
    }

    @PostMapping("/introspect")
    public JsonResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        return JsonResponse.success(authenticationService.introspect(request));
    }

    @PostMapping("/log-out")
    public JsonResponse<?> logout() throws ParseException {
        authenticationService.logout();
        return JsonResponse.success(null);
    }

    @PostMapping("/my-info")
    public JsonResponse<AccountResponse> getMyInfo() {
        return JsonResponse.success(userService.getMyInfo());
    }

}
