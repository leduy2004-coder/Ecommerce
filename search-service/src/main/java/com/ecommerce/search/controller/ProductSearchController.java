package com.ecommerce.search.controller;

import com.ecommerce.search.dto.ApiResponse;
import com.ecommerce.search.dto.PageResponse;
import com.ecommerce.search.dto.request.ProductSearchFilters;
import com.ecommerce.search.dto.response.ProductSearchResponse;
import com.ecommerce.search.exception.AppException;
import com.ecommerce.search.exception.ErrorCode;
import com.ecommerce.search.service.ESProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductSearchController {
    ESProductService esProductService;

    @GetMapping("/products")
    public ApiResponse<PageResponse<ProductSearchResponse>> searchItems(@RequestParam(required = false) String keyword,
                                                                        @RequestParam(required = false, defaultValue = "price") String sortBy,
                                                                        @RequestParam(required = false, defaultValue = "0") int page,
                                                                        @RequestParam(required = false, defaultValue = "desc") String order,
                                                                        @RequestParam(required = false, defaultValue = "60") int limit,
                                                                        @RequestParam(required = false) List<String> categoryIds,
                                                                        @RequestParam(required = false) Integer minPrice,
                                                                        @RequestParam(required = false) Integer maxPrice,
                                                                        @RequestParam(required = false) Integer minRating
    ) {
        log.info("keyword search: {}", keyword);
        if (minPrice != null && maxPrice != null && minPrice > maxPrice)
            throw new AppException(ErrorCode.INVALID_RANGE);

        ProductSearchFilters filters = new ProductSearchFilters( minPrice, maxPrice,minRating,categoryIds);
        PageResponse<ProductSearchResponse> response = esProductService.homepageSearch(keyword, sortBy, order, page, limit,
                filters);
        return ApiResponse.<PageResponse<ProductSearchResponse>>builder()
                .result(response)
                .build();
    }

}