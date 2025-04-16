package com.ecommerce.product.dto.response;

import com.ecommerce.product.utility.BannerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerGetResponse {
    String id;
    String userId;
    CloudinaryResponse imgUrl;
    Date dateStart;
    Date dateEnd;
    BannerStatus status;
}