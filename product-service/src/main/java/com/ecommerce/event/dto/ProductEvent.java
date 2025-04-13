package com.ecommerce.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ProductStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEvent {
    String id;
    String userId;
    String name;
    String thumbnailUrl;
    String affiliateLink;
    long price;
    Integer totalComment;
    double averageRating;
    String description;
    ProductStatus status;
    String categoryId;
    boolean visible;
    boolean deleted;
}