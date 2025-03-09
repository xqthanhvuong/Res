package com.manager.restaurant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.manager.restaurant.dto.request.OTPRequest;
import com.manager.restaurant.entity.OTP;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.helper.OTPHelper;
import com.manager.restaurant.mapper.OTPMapper;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.OTPRepository;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class OTPService {

    // Load from application.yaml
    @Value("${account-sid}")
    String accountSid;

    @Value("${auth-token}")
    String authToken;

    @Value("${phone:}")
    String senderPhone;

    // Inject OTP Repository
    final OTPRepository otpRepository;

    // Inject Account Repository
    final AccountRepository accountRepository;

    // Inject OTP Mapper
    final OTPMapper otpMapper;

    // Service to init twillop and send OTP to phone.
    public boolean sendSMS(String receivePhone, String code_otp) {
        try {
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                    new PhoneNumber(receivePhone),
                    new PhoneNumber(senderPhone),
                    "Your OTP code is: " + code_otp).create();
            return message.getSid() != null;
        } catch (TwilioException e) {
            log.error("Failed to send OTP via Twilio: {}", e.getMessage(), e);
            return false;
        }
    }

    // Service to gernerate, send and save OTP to database.
    @Transactional
    public void generateAndSendAndSaveOTP(OTPRequest request) {
        // Generate OTP code and create OTP entity.
        String otpCode = OTPHelper.generateOTP(5);
        String phone = request.getPhone();

        // Send SMS to phone of user, handle if it has error.
        boolean isSentSuccess = sendSMS(phone, otpCode);
        if (!isSentSuccess) {
            throw new BadException(ErrorCode.SMS_SEND_FAILED);
        }

        // Delete old OTP before saving new one
        if (otpRepository.existsByPhone(phone)) {
            otpRepository.deleteByPhone(phone);
        }

        // Set data for OTP entity
        OTP otp = otpMapper.toOTP(request);
        otp.setOtpCode(otpCode);
        otp.setUsed(false);

        // Save OTP entity to database.
        otpRepository.save(otp);
    }

    // Service to send OTP code to phone (get data from request).
    @Transactional
    public void sendOTP(OTPRequest request) {
        // Validation phone number of user.
        String phone = request.getPhone();
        if (phone == null || !accountRepository.existsByPhone(phone)) {
            throw new BadException(ErrorCode.PHONE_NOT_FOND);
        }

        // Send and save OTP.
        generateAndSendAndSaveOTP(request);
    }

    // Service to send OTP code to phone (get email from request, after get phone to
    // send).
    @Transactional
    public void sendOTPMail(OTPRequest request) {
        String email = request.getEmail();
        if (email == null || !accountRepository.existsByUsername(email)) {
            throw new BadException(ErrorCode.EMAIL_NOT_FOND);
        }

        String phone = accountRepository.findPhoneByUsername(email)
                .orElseThrow(() -> new BadException(ErrorCode.PHONE_NOT_FOND));

        // Send and save OTP.
        request.setPhone(phone);
        generateAndSendAndSaveOTP(request);
    }

    // Service to valiation OTP code. If it is valid, active account of user.
    @Transactional
    public void activeOTP(OTPRequest request) {
        // Validate OTP
        validateAndMarkOTP(request);

        // If OTP is valid, activate account.
        String phone = request.getPhone();
        int updated = accountRepository.activateAccountByPhone(phone);
        if (updated == 0) {
            throw new BadException(ErrorCode.ACTIVE_ACCOUNT_FAILED);
        }
    }

    // Service to validation OTP base on phone number and code.
    @Transactional
    public void validateAndMarkOTP(OTPRequest request) {
        // Get OTP code and phone.
        String otpCode = request.getOtpCode();
        String phone = request.getPhone();

        // Validate format of OTP code.
        if (!OTPHelper.isOTP(otpCode)) {
            throw new BadException(ErrorCode.OTP_INVALID_FORMAT);
        }

        // Validate OTP code in database.
        int updated = otpRepository.markOtpAsUsed(phone, otpCode);
        if (updated == 0) {
            throw new BadException(ErrorCode.OTP_INVALID_OR_USED);
        }
    }

}
