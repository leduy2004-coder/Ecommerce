package com.ecommerce.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductGetResponse {
    String id;
    String affiliateLink;
    Double price;
    String description;
    List<CloudinaryResponse> imgUrl;
}