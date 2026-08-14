package com.justjava.ecommerce.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender     mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")      private String fromAddress;
    @Value("${app.mail.from-name}") private String fromName;

    @Async("mailExecutor")
    public void sendHtml(String toAddress, String subject, String templateName, Map<String, Object> model) {
        if (toAddress == null || toAddress.isBlank()) {
            log.debug("Skipping email '{}' — no recipient address", subject);
            return;
        }
        sendToMany(List.of(toAddress), subject, templateName, model);
    }

    @Async("mailExecutor")
    public void sendHtmlToMany(Collection<String> recipients, String subject, String templateName, Map<String, Object> model) {
        sendToMany(recipients, subject, templateName, model);
    }

    private void sendToMany(Collection<String> recipients, String subject, String templateName, Map<String, Object> model) {
        List<String> cleaned = recipients == null ? List.of()
                : recipients.stream().filter(r -> r != null && !r.isBlank()).distinct().toList();
        if (cleaned.isEmpty()) {
            log.debug("Skipping email '{}' — no valid recipients", subject);
            return;
        }
        try {
            Context ctx = new Context();
            if (model != null) model.forEach(ctx::setVariable);
            String html = templateEngine.process("email/" + templateName, ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress, fromName);
            helper.setTo(cleaned.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Sent email '{}' to {} recipient(s)", subject, cleaned.size());
        } catch (Exception e) {
            log.warn("Failed to send email '{}' to {}: {}", subject, cleaned, e.getMessage());
        }
    }
}
