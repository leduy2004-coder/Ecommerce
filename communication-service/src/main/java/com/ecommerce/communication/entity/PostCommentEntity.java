package com.ecommerce.communication.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(value = "post_comment")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentEntity {
    @MongoId
    String id;
    String parentId;

    String postId;

    String comment;
    String userId;

    @CreatedDate
    @Field("created_date")
    Instant createdDate;


    @LastModifiedDate
    @Field("modified_date")
    Instant modifiedDate;
}