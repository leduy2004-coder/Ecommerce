package com.ecommerce.search.controller;


import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.search.service.ESProductService;
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
public class ProductEventListener {
    ESProductService esProductService;

    @KafkaListener(topics = "product-sync")
    public void listenNotificationDelivery(ProductEvent message) {
        log.info("Message received: {}", message);
        esProductService.indexOrUpdateProduct(message);
    }

}