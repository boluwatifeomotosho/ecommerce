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
@ConditionalOnExpression("'${sms.twilio.account-sid:}'.length() > 0")
public class TwilioSmsService implements SmsService {

    private final RestClient restClient;
    private final String     accountSid;
    private final String     fromNumber;

    public TwilioSmsService(
            RestClient.Builder builder,
            @Value("${sms.twilio.account-sid}") String accountSid,
            @Value("${sms.twilio.auth-token}")  String authToken,
            @Value("${sms.twilio.from-number}") String fromNumber
    ) {
        this.accountSid = accountSid;
        this.fromNumber = fromNumber;
        this.restClient = builder
                .baseUrl("https://api.twilio.com")
                .defaultHeaders(headers -> headers.setBasicAuth(accountSid, authToken))
                .build();
    }

    @Override
    public void sendOtp(String phone, String otp) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("From", fromNumber);
        form.add("To",   phone);
        form.add("Body", "Your NaijaMart code is: " + otp + ". Valid for 10 minutes. Do not share.");

        restClient.post()
                .uri("/2010-04-01/Accounts/{sid}/Messages.json", accountSid)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity();

        log.info("OTP sent via Twilio to {}", phone);
    }
}
