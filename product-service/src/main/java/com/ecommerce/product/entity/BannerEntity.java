package com.ecommerce.product.entity;

import com.ecommerce.product.utility.BannerStatus;
import com.ecommerce.product.utility.ProductStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Builder
@Document(value = "banner")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerEntity extends BaseEntity {
    String userId;
    Date dateStart;
    Date dateEnd;
    BannerStatus status;
}