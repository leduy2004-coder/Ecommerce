package com.ecommerce.identity.repository.feignClient;

import com.ecommerce.identity.config.security.AuthenticationRequestInterceptor;
import org.example.ProfileCreationRequest;
import org.example.ProfileCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profile-service", url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileCreationResponse createProfile(@RequestBody ProfileCreationRequest request);
}