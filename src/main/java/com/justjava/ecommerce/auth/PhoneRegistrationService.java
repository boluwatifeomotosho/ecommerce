package com.justjava.ecommerce.auth;

import com.justjava.ecommerce.auth.PhoneOtpSession;
import com.justjava.ecommerce.auth.PhoneOtpSessionRepository;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneRegistrationService {

    private static final int OTP_TTL_MINUTES     = 10;
    private static final int MAX_ATTEMPTS        = 5;
    private static final int MAX_REQUESTS_WINDOW = 3;
    private static final int RATE_WINDOW_MINUTES = 15;

    private final PhoneOtpSessionRepository otpRepo;
    private final UserRepository            userRepository;
    private final SmsService                smsService;

    public enum OtpResult { OK, INVALID, EXPIRED, MAX_ATTEMPTS }

    @Transactional
    public void sendOtp(String phone) {
        otpRepo.findByPhone(phone).ifPresent(existing -> {
            LocalDateTime windowEnd = existing.getWindowStart().plusMinutes(RATE_WINDOW_MINUTES);
            int count = LocalDateTime.now().isBefore(windowEnd) ? existing.getRequestCount() : 0;
            if (count >= MAX_REQUESTS_WINDOW) {
                throw new RateLimitException("Too many OTP requests. Please wait before trying again.");
            }
        });

        String otp  = generateOtp();
        String hash = sha256(otp);

        PhoneOtpSession session = otpRepo.findByPhone(phone).orElse(
                PhoneOtpSession.builder().phone(phone).windowStart(LocalDateTime.now()).requestCount(0).build()
        );

        LocalDateTime windowEnd = session.getWindowStart().plusMinutes(RATE_WINDOW_MINUTES);
        if (LocalDateTime.now().isAfter(windowEnd)) {
            session.setWindowStart(LocalDateTime.now());
            session.setRequestCount(0);
        }

        session.setOtpHash(hash);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES));
        session.setAttemptCount(0);
        session.setRequestCount(session.getRequestCount() + 1);
        otpRepo.save(session);

        smsService.sendOtp(phone, otp);
    }

    @Transactional
    public OtpResult verifyOtp(String phone, String submittedOtp) {
        return otpRepo.findByPhone(phone).map(session -> {
            if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
                return OtpResult.EXPIRED;
            }
            if (session.getAttemptCount() >= MAX_ATTEMPTS) {
                return OtpResult.MAX_ATTEMPTS;
            }
            if (!sha256(submittedOtp).equals(session.getOtpHash())) {
                session.setAttemptCount(session.getAttemptCount() + 1);
                otpRepo.save(session);
                return OtpResult.INVALID;
            }
            otpRepo.delete(session);
            return OtpResult.OK;
        }).orElse(OtpResult.EXPIRED);
    }

    public boolean isPhoneAlreadyRegistered(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) { super(message); }
    }
}
