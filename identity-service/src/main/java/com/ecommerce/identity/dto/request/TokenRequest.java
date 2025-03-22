package com.ecommerce.identity.dto.request;

import com.ecommerce.identity.dto.response.UserResponse;
import com.ecommerce.identity.utility.enumUtils.TokenType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenRequest {
    BigInteger id;
    String token;
    TokenType tokenType;
    boolean revoked;
    boolean expired;
    UserResponse userEntity;
}
