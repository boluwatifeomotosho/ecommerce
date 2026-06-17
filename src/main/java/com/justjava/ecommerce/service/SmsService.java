package com.justjava.ecommerce.service;

public interface SmsService {
    void sendOtp(String phone, String otp);
}
