package com.ecommerce.notification.controller;

import com.ecommerce.notification.dto.NotifyWebSKDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketController {

    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private-message")
    public NotifyWebSKDTO sendNotificationToUser(@Payload NotifyWebSKDTO message) {
        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), "/queue/notifications", message);

        log.info("✅ Sending to user {}: {}", message.getUserId(), message.getContent());

        return message;
    }
}
