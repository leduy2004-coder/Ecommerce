package com.ecommerce.communication.repository;

import com.ecommerce.communication.entity.ProductReviewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public interface ProductReviewRepository extends MongoRepository<ProductReviewEntity, String> {
}
