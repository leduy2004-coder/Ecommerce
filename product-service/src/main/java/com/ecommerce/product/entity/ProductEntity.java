package com.ecommerce.product.entity;

import com.ecommerce.product.utility.ProductStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(value = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEntity extends BaseEntity {
    String userId;
    String name;
    String affiliateLink;
    long price;
    String description;
    ProductStatus status;
    String attribute;
    String categoryId;
    boolean visible;
}