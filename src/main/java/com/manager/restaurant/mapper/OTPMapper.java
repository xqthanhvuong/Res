package com.manager.restaurant.mapper;

import org.mapstruct.Mapper;

import com.manager.restaurant.dto.request.OTPRequest;
import com.manager.restaurant.entity.OTP;

@Mapper(componentModel = "spring")
public interface OTPMapper {

    OTP toOTP(OTPRequest otpRequest);
    
}
