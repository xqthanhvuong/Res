package com.manager.restaurant.service;

import org.springframework.stereotype.Service;

import com.manager.restaurant.dto.request.PaymentRequest;
import com.manager.restaurant.dto.response.PaymentResponse;
import com.manager.restaurant.entity.Payment;
import com.manager.restaurant.entity.Restaurant;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.PaymentMapper;
import com.manager.restaurant.repository.PaymentRepository;
import com.manager.restaurant.repository.RestaurantRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentService {

    // Inject Payment Repository
    PaymentRepository paymentRepository;

    // Inject Payment Mapper
    PaymentMapper paymentMapper;

    RestaurantRepository restaurantRepository;

    // Service to create payment
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        if (paymentRequest == null) {
            throw new BadException(ErrorCode.INVALID_REQUEST);
        }
        Payment newPayment = Payment.builder()
                            .idPayment(paymentRequest.getIdPayment())
                            .restaurant(restaurantRepository.getReferenceById(paymentRequest.getIdRestaurant()))
                            .partnerCode(paymentRequest.getPartnerCode())
                            .accessKey(paymentRequest.getAccessKey())
                            .secretKey(paymentRequest.getSecretKey())
                            .build();
        Payment createdPayment = paymentRepository.save(newPayment);

        return paymentMapper.toPaymentResponse(createdPayment);
    }

    // Service to get payment
    public PaymentResponse getPayment(String accountId) {
        if (accountId == null) {
            throw new BadException(ErrorCode.INVALID_KEY);
        }

        Payment payment = paymentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BadException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentMapper.toPaymentResponse(payment);
    }

    // Service to update payment
    public PaymentResponse updatePayment(PaymentRequest paymentRequest) {
        String paymentId = paymentRequest.getIdPayment();
        if (paymentId == null) {
            throw new BadException(ErrorCode.INVALID_KEY);
        }

        // Check payment
        Payment existingPayment = paymentRepository.findByIdPayment(paymentId)
                .orElseThrow(() -> new BadException(ErrorCode.PAYMENT_NOT_FOUND));

        // Update info
        existingPayment.setPartnerCode(paymentRequest.getPartnerCode());
        existingPayment.setAccessKey(paymentRequest.getAccessKey());
        existingPayment.setSecretKey(paymentRequest.getSecretKey());

        // Update payment
        Payment updatedPayment = paymentRepository.save(existingPayment);

        return paymentMapper.toPaymentResponse(updatedPayment);
    }

}
