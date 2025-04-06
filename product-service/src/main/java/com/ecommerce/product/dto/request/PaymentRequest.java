package com.ecommerce.product.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
        String code;
        String message;
        String paymentUrl;
        String productId;
        Integer amount;
        String bankCode;
        String userId;
}
