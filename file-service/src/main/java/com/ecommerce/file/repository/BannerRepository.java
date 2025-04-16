package com.ecommerce.file.repository;


import com.ecommerce.file.entity.BannerImageEntity;
import com.ecommerce.file.entity.PostImageEntity;
import com.ecommerce.file.entity.ProductImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends MongoRepository<BannerImageEntity, String> {
    List<BannerImageEntity> findAllByBannerId(String bannerId);

}
