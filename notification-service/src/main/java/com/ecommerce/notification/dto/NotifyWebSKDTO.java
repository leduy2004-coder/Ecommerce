package com.ecommerce.notification.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotifyWebSKDTO {
    String userId;
    String productId;
    String content;
}