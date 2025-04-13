package com.ecommerce.profile.repository.httpClient;

import com.ecommerce.profile.config.security.AuthenticationRequestInterceptor;
import com.ecommerce.profile.dto.ApiResponse;
import org.example.CloudinaryResponse;
import org.example.ImageType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "file-service", url = "${app.services.file.url}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface FileClient {
    @GetMapping(value = "/internal/file/get-img")
    ApiResponse<List<CloudinaryResponse>> getImageProduct(@RequestParam("id") String id,
                                                          @RequestParam("type") ImageType imageType);

}

