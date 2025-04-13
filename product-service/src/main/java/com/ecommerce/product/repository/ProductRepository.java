package com.ecommerce.product.repository;


import com.ecommerce.product.entity.ProductEntity;
import org.example.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    @Query("{ 'userId': ?0, 'status': { $ne: ?1 } }")
    Page<ProductEntity> findAllByUserIdAndStatusNot(String userId, ProductStatus status, Pageable pageable);

    Optional<ProductEntity> findByIdAndUserId(String id, String userId);

}