package com.ecommerce.file.controller;

import com.ecommerce.file.dto.ApiResponse;
import com.ecommerce.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;
import org.example.ImageType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;

    @PostMapping("/media/upload")
    ApiResponse<CloudinaryResponse> uploadMedia(@RequestParam("file") MultipartFile file,
                                                @RequestParam("type") ImageType type,
                                                @RequestParam("id") String id)  {
        return ApiResponse.<CloudinaryResponse>builder()
                .result(fileService.uploadFile(file,type,id))
                .build();
    }
}