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
@Document(value = "banner_image")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerImageEntity extends BaseEntity{
    String bannerId;
    String name;
    String url;
    String publicId;
}