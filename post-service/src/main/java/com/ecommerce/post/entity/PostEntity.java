package com.ecommerce.post.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@Document(value = "post")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostEntity {
    @MongoId
    String id;
    String userId;
    String title;
    String content;
    String affiliateLink;
    Instant createdDate;
    Instant modifiedDate;
    List<String> hashTags;
}