package com.ecommerce.communication.repository;

import com.ecommerce.communication.entity.ProductCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductCommentRepository extends MongoRepository<ProductCommentEntity, String> {
    Page<ProductCommentEntity> findAllByProductId(String productId, Pageable pageable);
}
