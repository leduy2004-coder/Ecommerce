package com.ecommerce.file.entity;

import com.ecommerce.file.utility.AvatarType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(value = "user_image")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserImageEntity extends BaseEntity{
    String userId;
    String name;
    String url;
    String publicId;

    AvatarType avatarType;
}