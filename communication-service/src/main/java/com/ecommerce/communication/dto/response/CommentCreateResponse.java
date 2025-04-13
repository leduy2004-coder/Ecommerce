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
public class CommentCreateResponse {
        String productId;
        String comment;
        int rating;
        String created;
        int averageRating;
        int totalComment;
}
