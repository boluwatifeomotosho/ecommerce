package com.justjava.ecommerce.auth;

public interface SmsService {
    void sendOtp(String phone, String otp);
}
