package com.ecommerce.notification.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(value = "expired_product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductExpired extends BaseEntity {
    String userId;
    String productId;
    String content;
}