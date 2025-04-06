package com.ecommerce.notification.controller;

import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.notification.dto.request.NotifyRequest;
import com.ecommerce.notification.dto.request.SendEmailRequest;
import com.ecommerce.notification.dto.response.NotifyResponse;
import com.ecommerce.notification.service.EmailService;
import com.ecommerce.notification.service.ProductService;
import com.ecommerce.notification.utility.NotifyStatus;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;
    ProductService productService;
    SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message) throws MessagingException {
        log.info("Message received: {}", message);
        emailService.sendSimpleEmail(SendEmailRequest.builder()
                .toEmail(message.getRecipient())
                .subject(message.getSubject())
                .body(message.getBody())
                .build());
    }

    @KafkaListener(topics = "payment-expired-product")
    public void listenNotificationExpiredProduct(NotificationEvent message)  {
        log.info("Message expired: {}", message);
        NotifyResponse notifyResponse = productService.saveNotify(NotifyRequest.builder()
                .productId(message.getSubject())
                .userId(message.getRecipient())
                .content(message.getBody())
                .build(), NotifyStatus.EXPIRED);

        // Gửi WebSocket cho user sau khi xử lý xong
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/notifications",
                notifyResponse
        );

        log.info("✅ Sending to user expired {}: {}", message.getRecipient(), message.getBody());

    }

    @KafkaListener(topics = "notification-approved-product")
    public void listenNotificationApprovedProduct(NotificationEvent message) {
        log.info("Message approved: {}", message);
        NotifyResponse notifyResponse = productService.saveNotify(NotifyRequest.builder()
                .productId(message.getSubject())
                .userId(message.getRecipient())
                .content(message.getBody())
                .build(), NotifyStatus.APPROVED);

        // Gửi WebSocket cho user sau khi xử lý xong
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/notifications",
                notifyResponse
        );
        log.info("✅ Sending to user approved {}: {}", message.getRecipient(), message.getBody());

    }
}