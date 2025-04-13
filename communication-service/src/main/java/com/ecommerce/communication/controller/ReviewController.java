package com.ecommerce.communication.controller;

import com.ecommerce.communication.dto.ApiResponse;
import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.response.ReviewGetResponse;
import com.ecommerce.communication.service.CommentService;
import com.ecommerce.communication.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @GetMapping(value = "/product/get-review/{productId}")
    public ApiResponse<ReviewGetResponse> getReviewProduct(@PathVariable("productId") String productId) {

        return ApiResponse.<ReviewGetResponse>builder()
                .result(reviewService.getProductReviewAndComment(productId))
                .build();
    }
}