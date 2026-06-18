package com.justjava.ecommerce.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogSmsService implements SmsService {

    @Override
    public void sendOtp(String phone, String otp) {
        log.info("============================================================");
        log.info("  [DEV] OTP for {} → {}", phone, otp);
        log.info("  Configure an SMS provider to enable real delivery.");
        log.info("============================================================");
    }
}
