package com.ecommerce.search.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ProductStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "products")
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ESProduct {

    @Id
    String id;

    @Field(type = FieldType.Text)
    String name;

    @Field(type = FieldType.Text)
    String thumbnailUrl;

    @Field(type = FieldType.Keyword)
    String categoryId;

    @Field(type = FieldType.Long)
    long price;

    @Field(type = FieldType.Integer)
    Integer totalComment;

    @Field(type = FieldType.Double)
    double averageRating;

    @Field(type = FieldType.Date)
    Instant createdDate;

    @Field(type = FieldType.Text)
    String description;

    @Field(type = FieldType.Keyword)
    ProductStatus status;

    @Field(type = FieldType.Boolean)
    boolean visible;
}
