package com.ecommerce.notification.controller;


import com.ecommerce.notification.dto.ApiResponse;
import com.ecommerce.notification.dto.request.EmailRequest;
import com.ecommerce.notification.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/email/send")
    ApiResponse<Boolean> sendEmail(@RequestBody EmailRequest request) throws MessagingException {
        return ApiResponse.<Boolean>builder()
                .result(emailService.sendSimpleEmail(request))
                .build();
    }
    @KafkaListener(topics = "onboard-successful")
    public void listen(String message){
        log.info("Message received: {}", message);
    }
}