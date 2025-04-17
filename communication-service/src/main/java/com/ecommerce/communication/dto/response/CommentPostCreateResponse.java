package com.ecommerce.communication.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentPostCreateResponse {
        String postId;
        String comment;
        String parentId;
        String id;
        String created;
}
