package com.ecommerce.identity.dto.response;

import com.ecommerce.identity.utility.enumUtils.TokenType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private String id;
    private String refreshToken;
    private TokenType tokenType;
    private boolean revoked;
    private boolean expired;
}
