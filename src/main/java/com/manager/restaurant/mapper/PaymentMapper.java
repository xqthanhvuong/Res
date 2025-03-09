package com.manager.restaurant.mapper;

import org.mapstruct.Mapper;

import com.manager.restaurant.dto.request.PaymentRequest;
import com.manager.restaurant.dto.response.PaymentResponse;
import com.manager.restaurant.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    Payment toPayment(PaymentRequest paymentRequest);

    Payment toPayment(PaymentResponse paymentResponse);

    PaymentResponse toPaymentResponse(Payment payment);
}
