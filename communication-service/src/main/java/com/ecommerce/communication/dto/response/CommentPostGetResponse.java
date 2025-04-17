package com.ecommerce.communication.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.ProfileGetResponse;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentPostGetResponse {
        String id;
        String comment;
        String parentId;
        String created;
        ProfileGetResponse user;
}
