package com.ecommerce.identity.repository.feignClient;

import com.ecommerce.identity.dto.response.Oauth2UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "google-user-info", url = "https://www.googleapis.com")
public interface GoogleUserInfoClient {
    @GetMapping("/oauth2/v2/userinfo")
    Oauth2UserResponse.GoogleUserInfo getUserInfo(@RequestParam("access_token") String accessToken);
}