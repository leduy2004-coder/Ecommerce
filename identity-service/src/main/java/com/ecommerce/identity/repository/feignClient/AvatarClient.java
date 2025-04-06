package com.ecommerce.identity.repository.feignClient;

import com.ecommerce.identity.config.security.AuthenticationRequestInterceptor;
import org.example.CloudinaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "file-service", url = "${app.services.file}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface AvatarClient {
    @PostMapping(value = "/internal/file/create-avatar", produces = MediaType.APPLICATION_JSON_VALUE)
    CloudinaryResponse createAvatar(@RequestParam("url") String url,
                                     @RequestParam("userId") String userId);
}