package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ApiResponse;
import com.ecommerce.product.dto.request.ProductCreateRequest;
import com.ecommerce.product.dto.response.ProductCreateResponse;
import com.ecommerce.product.dto.response.ProductGetResponse;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.utility.ProductStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @GetMapping(value = "/get-product/{productId}")
    public ApiResponse<ProductGetResponse> getProductById(@PathVariable("productId") String productId) {

        ProductGetResponse response = productService.getProductById(productId);

        return ApiResponse.<ProductGetResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductCreateResponse> createProduct(
            @RequestPart("request") ProductCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return ApiResponse.<ProductCreateResponse>builder()
                .result(productService.createProduct(request, files))
                .build();
    }

    @PostMapping(value = "/delete-of-user")
    public ApiResponse<Boolean> deleteProduct(
            @RequestParam("productId") String productId) {

        return ApiResponse.<Boolean>builder()
                .result(productService.deleteProductOfUser(productId))
                .build();
    }
    @DeleteMapping(value = "/delete-of-admin")
    public ApiResponse<Boolean> deleteProductOfAdmin(
            @RequestParam("productId") String productId) {

        return ApiResponse.<Boolean>builder()
                .result(productService.deleteProductOfAdmin(productId))
                .build();
    }

    @PostMapping(value = "/accept/{productId}")
    public ApiResponse<ProductStatus> acceptProduct(@PathVariable("productId") String productId) {

        ProductStatus response = productService.acceptProduct(productId);

        return ApiResponse.<ProductStatus>builder()
                .result(response)
                .build();
    }


}