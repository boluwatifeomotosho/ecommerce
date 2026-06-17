package com.justjava.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnMissingBean(TermiiSmsService.class)
public class LogSmsService implements SmsService {

    @Override
    public void sendOtp(String phone, String otp) {
        log.info("============================================================");
        log.info("  [DEV] OTP for {} → {}", phone, otp);
        log.info("  Set sms.termii.api-key to enable real SMS delivery.");
        log.info("============================================================");
    }
}
