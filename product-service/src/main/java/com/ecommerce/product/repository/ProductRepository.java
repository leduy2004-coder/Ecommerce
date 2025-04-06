package com.ecommerce.product.repository;


import com.ecommerce.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    Page<ProductEntity> findAllByUserId(String userId, Pageable pageable);
    Optional<ProductEntity> findByIdAndUserId(String id, String userId);

}