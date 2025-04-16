package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ApiResponse;
import com.ecommerce.product.dto.PageResponse;
import com.ecommerce.product.dto.request.BannerCreateRequest;
import com.ecommerce.product.dto.request.ProductCreateRequest;
import com.ecommerce.product.dto.response.BannerGetActiveResponse;
import com.ecommerce.product.dto.response.BannerGetResponse;
import com.ecommerce.product.dto.response.ProductCreateResponse;
import com.ecommerce.product.dto.response.ProductGetResponse;
import com.ecommerce.product.service.BannerService;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.utility.BannerStatus;
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
@RequestMapping("/banners")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerController {
    BannerService bannerService;

    @GetMapping("/get-all")
    public ApiResponse<PageResponse<BannerGetResponse>> getAllBanner(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BannerGetResponse> response = bannerService.getAllBanner(page, size);
        return ApiResponse.<PageResponse<BannerGetResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/my-banners")
    public ApiResponse<List<BannerGetResponse>> getBannerByUser() {
        List<BannerGetResponse> response = bannerService.getMyBanners();
        return ApiResponse.<List<BannerGetResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/get-by-user")
    public ApiResponse<List<BannerGetResponse>> getBannerByUser(
            @RequestParam String userId
    ) {
        List<BannerGetResponse> response = bannerService.getBannersByUserId(userId);
        return ApiResponse.<List<BannerGetResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping(value = "/active")
    public ApiResponse<BannerGetResponse> getBannerActive() {

        BannerGetResponse response = bannerService.getBannerByStatus(BannerStatus.ACTIVE);

        return ApiResponse.<BannerGetResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping(value = "/active-or-payment")
    public ApiResponse<List<BannerGetActiveResponse>> getActiveOrPaymentBanners() {

        List<BannerGetActiveResponse> response = bannerService.getActiveOrPaymentBanners();

        return ApiResponse.<List<BannerGetActiveResponse>>builder()
                .result(response)
                .build();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BannerGetResponse> createProduct(
            @RequestPart("request") BannerCreateRequest request,
            @RequestPart(value = "files", required = false) MultipartFile files) {

        return ApiResponse.<BannerGetResponse>builder()
                .result(bannerService.createBanner(request, files))
                .build();
    }

    @DeleteMapping(value = "/delete")
    public ApiResponse<Boolean> deleteBanner(
                @RequestParam("bannerId") String bannerId) {

        return ApiResponse.<Boolean>builder()
                .result(bannerService.deleteBanner(bannerId))
                .build();
    }

    @PostMapping(value = "/accept/{bannerId}")
    public ApiResponse<BannerStatus> acceptProduct(@PathVariable("bannerId") String bannerId) {

        BannerStatus response = bannerService.acceptBanner(bannerId);

        return ApiResponse.<BannerStatus>builder()
                .result(response)
                .build();
    }


}