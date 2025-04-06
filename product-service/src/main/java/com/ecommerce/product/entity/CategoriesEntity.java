package com.ecommerce.product.entity;

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
@Document(value = "categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoriesEntity extends BaseEntity{
    String name;
}