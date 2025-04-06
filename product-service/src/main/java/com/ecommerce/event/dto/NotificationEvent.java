package com.ecommerce.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ChannelNotify;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    ChannelNotify channel;
    String recipient;
    String templateCode;
    Map<String, Object> param;
    String subject;
    String body;
}