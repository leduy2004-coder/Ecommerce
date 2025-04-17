package com.ecommerce.search.controller;


import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.event.dto.TagEvent;
import com.ecommerce.search.service.ESProductService;
import com.ecommerce.search.service.ESTagService;
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
public class EventListener {
    ESProductService esProductService;
    ESTagService esTagService;

    @KafkaListener(topics = "product-sync")
    public void listenNotificationDelivery(ProductEvent message) {
        log.info("Message received: {}", message);
        esProductService.indexOrUpdateProduct(message);
    }

    @KafkaListener(topics = "tag-sync")
    public void listenNotificationDeliveryTag(TagEvent message) {
        log.info("Message tag received: {}", message);
        esTagService.indexOrUpdateTag(message);
    }
}