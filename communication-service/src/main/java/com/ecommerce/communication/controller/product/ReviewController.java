package com.ecommerce.communication.controller.product;

import com.ecommerce.communication.dto.ApiResponse;
import com.ecommerce.communication.dto.response.ReviewGetResponse;
import com.ecommerce.communication.service.product.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/product")
public class ReviewController {
    ReviewService reviewService;

    @GetMapping(value = "/get-review/{productId}")
    public ApiResponse<ReviewGetResponse> getReviewProduct(@PathVariable("productId") String productId) {

        return ApiResponse.<ReviewGetResponse>builder()
                .result(reviewService.getProductReviewAndComment(productId))
                .build();
    }
}