package com.ecommerce.file.controller.internal;


import com.ecommerce.file.dto.ApiResponse;
import com.ecommerce.file.service.FileService;
import com.ecommerce.file.utility.ImageType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalFileController {
    FileService fileService;

    @PostMapping("/internal/post/upload")
    ApiResponse<CloudinaryResponse> uploadMediaPost(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("postId") String postId) throws IOException {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.POST, postId))
                .build();
    }
    @PostMapping("/internal/user/upload")
    ApiResponse<CloudinaryResponse> uploadMediaUser(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") String userId) throws IOException {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.AVATAR, userId))
                .build();
    }
    @PostMapping("/internal/product/upload")
    ApiResponse<CloudinaryResponse> uploadMediaProduct(@RequestParam("file") MultipartFile file,
                                           @RequestParam("productId") String productId) throws IOException {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.PRODUCT, productId))
                .build();
    }
}