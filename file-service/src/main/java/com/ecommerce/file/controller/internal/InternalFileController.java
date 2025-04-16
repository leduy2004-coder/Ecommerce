package com.ecommerce.file.controller.internal;


import com.ecommerce.file.dto.ApiResponse;
import com.ecommerce.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;
import org.example.FileDeleteRequest;
import org.example.ImageType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalFileController {
    FileService fileService;

    @GetMapping(value = "/file/get-img")
    public ApiResponse<List<CloudinaryResponse>> getImageProduct(@RequestParam("id") String id,
                                                               @RequestParam("type") ImageType type) {

        List<CloudinaryResponse> response = fileService.getAllById(id, type);

        return ApiResponse.<List<CloudinaryResponse>>builder()
                .result(response)
                .build();
    }

    @PostMapping("/file/post/upload")
    ApiResponse<CloudinaryResponse> uploadMediaPost(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("postId") String postId){
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.POST, postId))
                .build();
    }
    @PostMapping("/file/banner/upload")
    ApiResponse<CloudinaryResponse> uploadBanner(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("bannerId") String bannerId){
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.BANNER, bannerId))
                .build();
    }
    @PostMapping("/file/create-avatar")
    ApiResponse<CloudinaryResponse> uploadMediaUser(@RequestParam("url") String url,
                                        @RequestParam("userId") String userId){
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.createAvatar(userId,url))
                .build();
    }
    @PostMapping("/file/user/upload")
    ApiResponse<CloudinaryResponse> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("userId") String userId)  {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.AVATAR, userId))
                .build();
    }
    @PostMapping(value = "/file/product/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<CloudinaryResponse> uploadMediaProduct(@RequestPart("file") MultipartFile file ,
                                           @RequestPart("productId") String productId) {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file, ImageType.PRODUCT, productId))
                .build();
    }

    @DeleteMapping(value = "/file/delete-img")
    public ApiResponse<Boolean> deleteImageProduct(@RequestBody FileDeleteRequest request) {

        Boolean response = fileService.deleteById(request.getId(), request.getType());
        return ApiResponse.<Boolean>builder()
                .result(response)
                .build();
    }
}