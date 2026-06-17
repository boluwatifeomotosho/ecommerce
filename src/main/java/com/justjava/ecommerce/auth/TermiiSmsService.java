package com.justjava.ecommerce.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "sms.termii.api-key")
public class TermiiSmsService implements SmsService {

    private final RestClient restClient;
    private final String apiKey;
    private final String senderId;

    public TermiiSmsService(
            RestClient.Builder builder,
            @Value("${sms.termii.api-key}") String apiKey,
            @Value("${sms.termii.sender-id:NaijaMart}") String senderId
    ) {
        this.apiKey = apiKey;
        this.senderId = senderId;
        this.restClient = builder.baseUrl("https://v3.api.termii.com").build();
    }

    @Override
    public void sendOtp(String phone, String otp) {
        Map<String, Object> body = Map.of(
                "to", phone,
                "from", senderId,
                "sms", "Your NaijaMart verification code is: " + otp + ". Valid for 10 minutes. Do not share this code.",
                "type", "plain",
                "api_key", apiKey,
                "channel", "dnd"
        );

        restClient.post()
                .uri("/api/sms/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        log.info("OTP sent via Termii to {}", phone);
    }
}
