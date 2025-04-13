package com.ecommerce.product.repository.httpClient;

import com.ecommerce.product.config.security.AuthenticationRequestInterceptor;
import com.ecommerce.product.dto.ApiResponse;
import org.example.ProductGetReview;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "communication-service", url = "${app.services.communication.url}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface CommunicationClient {
    @GetMapping(value = "/internal/product/get-review")
    ApiResponse<ProductGetReview> getImageProduct(@RequestParam("productId") String productId);

}

