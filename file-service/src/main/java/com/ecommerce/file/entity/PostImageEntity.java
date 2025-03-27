package com.ecommerce.file.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(value = "post_image")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostImageEntity extends BaseEntity{
    String postId;
    private String name;
    private String url;
    private String publicId;
}