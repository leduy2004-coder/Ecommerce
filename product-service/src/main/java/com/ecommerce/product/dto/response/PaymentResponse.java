package com.ecommerce.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
       String code;
       String message;
       String paymentUrl;

       Integer amount;
       String bankCode;
       String userId;

}
