package com.ecommerce.identity.repository.feignClient;


import com.ecommerce.identity.dto.response.ExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "google-identity", url = "https://oauth2.googleapis.com")
public interface GoogleIdentityClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse.ExchangeTokenGoogle exchangeToken(@RequestParam Map<String, String> request);
}
