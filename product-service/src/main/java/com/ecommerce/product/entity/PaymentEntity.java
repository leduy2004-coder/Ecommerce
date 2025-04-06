package com.ecommerce.product.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(value = "payment")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentEntity extends BaseEntity {
    String userId;
    String productId;
    String code;
    Integer amount;
    String bankCode;
    Instant expiryDate;
    Boolean status;

}