package com.ecommerce.product.repository;


import com.ecommerce.product.entity.BannerEntity;
import com.ecommerce.product.entity.ProductEntity;
import com.ecommerce.product.utility.BannerStatus;
import org.example.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends MongoRepository<BannerEntity, String> {

    Optional<BannerEntity> findByStatus(BannerStatus status);

    @Query("{ 'status': { $in: ['PAYMENT', 'ACTIVE'] }, 'dateStart': { $lte: ?1 }, 'dateEnd': { $gte: ?0 } }")
    List<BannerEntity> findActiveOrPaymentBannerOverlap(Date newStart, Date newEnd);

    @Query("{ 'status': { $in: ['PAYMENT', 'ACTIVE'] } }")
    List<BannerEntity> findActiveOrPaymentBanners();

    List<BannerEntity> findAllByUserId(String userId);


}