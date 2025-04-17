package com.ecommerce.communication.controller.product;

import com.ecommerce.communication.dto.ApiResponse;
import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.request.CommentProductCreateRequest;
import com.ecommerce.communication.dto.response.CommentProductCreateResponse;
import com.ecommerce.communication.dto.response.CommentProductGetResponse;
import com.ecommerce.communication.service.product.ProductCommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentProductController {
    ProductCommentService commentService;

    @PostMapping(value = "/product/create-comment")
    public ApiResponse<CommentProductCreateResponse> createCommentProduct(@RequestBody CommentProductCreateRequest request) {

        return ApiResponse.<CommentProductCreateResponse>builder()
                .result(commentService.saveProductReview(request))
                .build();
    }

    @GetMapping("/product/get-comment/{productId}")
    ApiResponse<PageResponse<CommentProductGetResponse>> getCommentProduct(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @PathVariable("productId") String productId
    ){
        return ApiResponse.<PageResponse<CommentProductGetResponse>>builder()
                .result(commentService.getCommentProduct(page, size, productId))
                .build();
    }

}