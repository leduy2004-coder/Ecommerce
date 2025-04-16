package com.ecommerce.product.entity;

import com.ecommerce.product.utility.PaymentStatus;
import com.ecommerce.product.utility.PaymentType;
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
    String targetId;
    String code;
    Integer amount;
    String bankCode;
    Instant expiryDate;
    PaymentStatus status;
    PaymentType type;
}