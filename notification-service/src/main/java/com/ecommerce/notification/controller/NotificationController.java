package com.ecommerce.notification.controller;

import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.notification.dto.request.SendEmailRequest;
import com.ecommerce.notification.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message) throws MessagingException {
        log.info("Message received: {}", message);
        emailService.sendSimpleEmail(SendEmailRequest.builder()
                .toEmail(message.getRecipient())
                .subject(message.getSubject())
                .body(message.getBody())
                .build());
    }
}