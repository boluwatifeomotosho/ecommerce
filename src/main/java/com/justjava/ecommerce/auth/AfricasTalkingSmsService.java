package com.justjava.ecommerce.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@ConditionalOnExpression("'${sms.africastalking.api-key:}'.length() > 0")
public class AfricasTalkingSmsService implements SmsService {

    private final RestClient restClient;
    private final String     apiKey;
    private final String     username;
    private final String     senderId;

    public AfricasTalkingSmsService(
            RestClient.Builder builder,
            @Value("${sms.africastalking.api-key}")          String apiKey,
            @Value("${sms.africastalking.username:sandbox}") String username,
            @Value("${sms.africastalking.sender-id:}")       String senderId
    ) {
        this.apiKey   = apiKey;
        this.username = username;
        this.senderId = senderId;
        this.restClient = builder
                .baseUrl("https://api.africastalking.com")
                .defaultHeader("apiKey", apiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Override
    public void sendOtp(String phone, String otp) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", username);
        form.add("to",       phone);
        form.add("message",  "Your NaijaMart code is: " + otp + ". Valid for 10 minutes. Do not share.");
        if (senderId != null && !senderId.isBlank()) {
            form.add("from", senderId);
        }

        restClient.post()
                .uri("/version1/messaging")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity();

        log.info("OTP sent via Africa's Talking to {}", phone);
    }
}
