package com.ecommerce.communication.dto.response;

import com.ecommerce.communication.dto.PageResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewGetResponse {
    int averageRating;
    int totalComment;

    Map<Integer, Long> countAverageRatings;
    PageResponse<CommentProductGetResponse> listComments;
}



