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
public class CommentProductGetResponse {
        String id;
        String comment;
        int rating;
        String created;
        ProfileGetResponse user;
}
