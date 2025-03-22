package com.ecommerce.ApiGateway.service;


import com.ecommerce.ApiGateway.dto.ApiResponse;
import com.ecommerce.ApiGateway.dto.request.IntrospectRequest;
import com.ecommerce.ApiGateway.dto.response.IntrospectResponse;
import com.ecommerce.ApiGateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder()
                .token(token)
                .build());
    }
}