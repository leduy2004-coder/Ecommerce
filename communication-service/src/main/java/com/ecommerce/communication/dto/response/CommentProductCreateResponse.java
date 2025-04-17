package com.ecommerce.communication.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentProductCreateResponse {
        String productId;
        String comment;
        int rating;
        String id;
        String created;
        int averageRating;
        int totalComment;
}
