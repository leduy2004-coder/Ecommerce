package com.ecommerce.communication.controller.post;

import com.ecommerce.communication.dto.ApiResponse;
import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.request.CommentPostCreateRequest;
import com.ecommerce.communication.dto.response.CommentPostCreateResponse;
import com.ecommerce.communication.dto.response.CommentPostGetResponse;
import com.ecommerce.communication.service.post.PostCommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/post")
public class CommentPostController {
    PostCommentService commentService;

    @PostMapping(value = "/create-comment")
    public ApiResponse<CommentPostCreateResponse> createCommentPost(@RequestBody CommentPostCreateRequest request) {

        return ApiResponse.<CommentPostCreateResponse>builder()
                .result(commentService.saveCommentPost(request))
                .build();
    }

    @GetMapping("/get-comment/{postId}")
    ApiResponse<PageResponse<CommentPostGetResponse>> getCommentPost(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @PathVariable("postId") String postId
    ){
        return ApiResponse.<PageResponse<CommentPostGetResponse>>builder()
                .result(commentService.getCommentPost(page, size, postId))
                .build();
    }

}