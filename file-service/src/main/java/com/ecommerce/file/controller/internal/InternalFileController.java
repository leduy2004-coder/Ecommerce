package com.ecommerce.file.controller.internal;


import com.ecommerce.file.dto.ApiResponse;
import com.ecommerce.file.service.FileService;
import com.ecommerce.file.utility.ImageType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalFileController {
    FileService fileService;

    @PostMapping("/internal/file/post/upload")
    ApiResponse<CloudinaryResponse> uploadMediaPost(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("postId") String postId) throws IOException {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.POST, postId))
                .build();
    }

    @PostMapping("/internal/file/create-avatar")
    ApiResponse<CloudinaryResponse> uploadMediaUser(@RequestParam("url") String url,
                                        @RequestParam("userId") String userId) throws IOException {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.createAvatar(userId,url))
                .build();
    }
    @PostMapping("/internal/file/user/upload")
    ApiResponse<CloudinaryResponse> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("userId") String userId)  {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.AVATAR, userId))
                .build();
    }
    @PostMapping(value = "/internal/file/product/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<CloudinaryResponse> uploadMediaProduct(@RequestPart("file") MultipartFile file ,
                                           @RequestPart("productId") String productId) {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.PRODUCT, productId))
                .build();
    }
}