package com.justjava.ecommerce.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class PaystackService {

    private static final String BASE_URL = "https://api.paystack.co";

    private final RestClient restClient;

    public PaystackService(@Value("${paystack.secret-key}") String secretKey) {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + secretKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Calls Paystack /transaction/initialize and returns the authorization URL
     * to redirect the customer to.
     *
     * @param email       customer email (Paystack requires one; falls back to reference if absent)
     * @param amountNgn   order total in Naira — converted to kobo internally
     * @param reference   unique order reference
     * @param callbackUrl where Paystack redirects after payment
     */
    public String initializeTransaction(String email, BigDecimal amountNgn, String reference, String callbackUrl) {
        long amountKobo = amountNgn.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, Object> body = Map.of(
                "email",        email,
                "amount",       amountKobo,
                "reference",    reference,
                "callback_url", callbackUrl
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("/transaction/initialize")
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("status"))) {
            throw new IllegalStateException("Paystack initialization failed: " + response);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        return (String) data.get("authorization_url");
    }

    /**
     * Verifies a transaction and returns a {@link PaystackVerification} with
     * status and channel info.
     */
    public PaystackVerification verifyTransaction(String reference) {
        log.info("Verifying Paystack transaction: {}", reference);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.get()
                .uri("/transaction/verify/{reference}", reference)
                .retrieve()
                .body(Map.class);

        log.info("Paystack verify response: {}", response);

        if (response == null || !Boolean.TRUE.equals(response.get("status"))) {
            log.warn("Paystack verification failed — top-level status not true: {}", response);
            return new PaystackVerification(false, null);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data    = (Map<String, Object>) response.get("data");
        String              txStatus = (String) data.get("status");
        String              channel  = (String) data.get("channel");

        log.info("Paystack tx status: {}, channel: {}", txStatus, channel);
        return new PaystackVerification("success".equals(txStatus), channel);
    }

    public record PaystackVerification(boolean success, String channel) {}
}
