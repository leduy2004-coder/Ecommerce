package com.ecommerce.identity.controller;

import com.ecommerce.identity.dto.request.AuthenticationRequest;
import com.ecommerce.identity.dto.ApiResponse;
import com.ecommerce.identity.dto.response.AuthenticationResponse;
import com.ecommerce.identity.service.OAuth2UserService;
import com.ecommerce.identity.service.security.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.IntrospectRequest;
import org.example.IntrospectResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthenticationController {

    AuthenticationService service;
    OAuth2UserService oAuth2UserService;

    @PostMapping("/authenticate")
    public ApiResponse<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ApiResponse.<AuthenticationResponse>builder().result(service.authenticate(request)).build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) {
        var result = service.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }
    @PostMapping("/refresh-token")
    public ApiResponse<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        return ApiResponse.<AuthenticationResponse>builder().result(service.refreshToken(request, response)).build();
    }
    @PostMapping("/oauth2")
    ApiResponse<AuthenticationResponse> outboundAuthenticate(
            @RequestParam("code") String code,
            @RequestParam("provider") String provider
    ){
        var result = oAuth2UserService.getUserInfo(provider,code);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

}