package com.manager.restaurant.helper;

import java.security.SecureRandom;

public class OTPHelper {

    // Method to generate OTP with length.
    public static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    // Method to validation OTP.
    public static boolean isOTP(String otp) {
        return otp.matches("[0-9]{5}");
    }

}
