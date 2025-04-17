package com.ecommerce.post.controller;

import com.ecommerce.post.dto.ApiResponse;
import com.ecommerce.post.dto.PageResponse;
import com.ecommerce.post.dto.request.PostRequest;
import com.ecommerce.post.dto.response.PostResponse;
import com.ecommerce.post.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping("/create")
    ApiResponse<PostResponse> createPost(@RequestBody PostRequest request){
        return ApiResponse.<PostResponse>builder()
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/my-posts")
    ApiResponse<PageResponse<PostResponse>> myPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ){
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .result(postService.getMyPosts(page, size))
                .build();
    }
    @GetMapping("/get-posts-by-tag")
    public ApiResponse<PageResponse<PostResponse>> getPostsByTag(
            @RequestParam List<String> tags,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .result(postService.getPostsByTag(tags, page, size))
                .build();
    }



}