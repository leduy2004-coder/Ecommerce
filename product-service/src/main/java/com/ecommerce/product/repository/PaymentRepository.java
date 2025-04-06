package com.ecommerce.product.repository;

import com.ecommerce.product.entity.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentEntity, String> {
    List<PaymentEntity> findByExpiryDateBefore(Instant date);
}
