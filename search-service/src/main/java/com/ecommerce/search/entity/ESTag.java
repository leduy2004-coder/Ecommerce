package com.ecommerce.search.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ProductStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "tags")
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ESTag {

    @Id
    String id;

    @Field(type = FieldType.Text)
    String name;

    @Field(type = FieldType.Integer)
    Integer count;

}
