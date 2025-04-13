package com.ecommerce.communication.controller;

import com.ecommerce.communication.dto.ApiResponse;
import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.request.CommentCreateRequest;
import com.ecommerce.communication.dto.response.CommentCreateResponse;
import com.ecommerce.communication.dto.response.CommentGetResponse;
import com.ecommerce.communication.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping(value = "/product/create-comment")
    public ApiResponse<CommentCreateResponse> createCommentProduct(@RequestBody CommentCreateRequest request) {

        return ApiResponse.<CommentCreateResponse>builder()
                .result(commentService.saveProductReview(request))
                .build();
    }

    @GetMapping("/product/get-comment/{productId}")
    ApiResponse<PageResponse<CommentGetResponse>> myPosts(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @PathVariable("productId") String productId
    ){
        return ApiResponse.<PageResponse<CommentGetResponse>>builder()
                .result(commentService.getCommentProduct(page, size, productId))
                .build();
    }

}