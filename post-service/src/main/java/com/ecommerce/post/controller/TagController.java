package com.ecommerce.post.controller;

import com.ecommerce.post.dto.ApiResponse;
import com.ecommerce.post.dto.PageResponse;
import com.ecommerce.post.dto.request.PostRequest;
import com.ecommerce.post.dto.response.PostResponse;
import com.ecommerce.post.service.PostService;
import com.ecommerce.post.service.TagService;
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
@RequestMapping("/tags")
public class TagController {
    TagService tagService;


    @GetMapping("/top")
    ApiResponse<List<String>> getTopTags(){
        return ApiResponse.<List<String>>builder()
                .result(tagService.getTopTag())
                .build();
    }
}