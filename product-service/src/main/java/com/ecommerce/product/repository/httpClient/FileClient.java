package com.ecommerce.product.repository.httpClient;

import com.ecommerce.product.config.security.AuthenticationRequestInterceptor;
import com.ecommerce.product.dto.ApiResponse;
import org.example.CloudinaryResponse;
import org.example.FileDeleteRequest;
import org.example.ImageType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "file-service", url = "${app.services.file.url}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface FileClient {
    @PostMapping(value = "/internal/file/product/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<CloudinaryResponse> uploadMediaProduct(@RequestPart("file") MultipartFile file,
                                                       @RequestPart("productId") String productId);

    @PostMapping(value = "/internal/file/banner/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<CloudinaryResponse> uploadBanner(@RequestPart("file") MultipartFile file,
                                                 @RequestPart("bannerId") String bannerId);

    @GetMapping(value = "/internal/file/get-img")
    ApiResponse<List<CloudinaryResponse>> getImage(@RequestParam("id") String id,
                                                          @RequestParam("type") ImageType imageType);

    @DeleteMapping(value = "/internal/file/delete-img")
    ApiResponse<Boolean> deleteImageProduct(@RequestBody FileDeleteRequest request);
}

