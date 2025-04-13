package com.ecommerce.communication.repository.httpClient;//package com.ecommerce.post.repository.httpClient;

import com.ecommerce.communication.config.security.AuthenticationRequestInterceptor;
import com.ecommerce.communication.dto.ApiResponse;
import org.example.ProfileGetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileGetResponse> getProfile(@PathVariable String userId);
}