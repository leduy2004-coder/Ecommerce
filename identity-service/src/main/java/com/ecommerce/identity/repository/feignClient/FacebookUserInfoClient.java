package com.ecommerce.identity.repository.feignClient;

import com.ecommerce.identity.dto.response.Oauth2UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebook-user-info", url = "https://graph.facebook.com")
public interface FacebookUserInfoClient {
    @GetMapping("/me")
    Oauth2UserResponse.FacebookUserInfo getUserInfo(
            @RequestParam("access_token") String accessToken,
            @RequestParam("fields") String fields);
}