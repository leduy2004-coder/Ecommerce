package com.ecommerce.ApiGateway.repository;

import com.ecommerce.ApiGateway.dto.ApiResponse;
import com.ecommerce.ApiGateway.dto.request.IntrospectRequest;
import com.ecommerce.ApiGateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}