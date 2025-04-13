package com.ecommerce.communication.controller.internal;


import com.ecommerce.communication.dto.ApiResponse;
import com.ecommerce.communication.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ProductGetReview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalCommunicationController {
    ReviewService reviewService;

    @GetMapping(value = "/product/get-review")
    public ApiResponse<ProductGetReview> getImageProduct(@RequestParam("productId") String productId) {

        ProductGetReview response = reviewService.getProductReview(productId);

        return ApiResponse.<ProductGetReview>builder()
                .result(response)
                .build();
    }


}