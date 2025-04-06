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
@Document(value = "approved_product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductApproved extends BaseEntity {
    String userId;
    String productId;
    String content;
}