package com.ecommerce.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateResponse {
    String id;
    String affiliateLink;
    Double price;
    String description;
    List<String> imgUrl;
}