package com.ecommerce.communication.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(value = "product_review")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReviewEntity {
    @Id
    String productId;

    int averageRating;
    int totalComment;
}