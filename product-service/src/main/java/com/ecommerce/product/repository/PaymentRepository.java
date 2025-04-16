package com.ecommerce.product.repository;

import com.ecommerce.product.entity.PaymentEntity;
import com.ecommerce.product.utility.PaymentType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentEntity, String> {
    List<PaymentEntity> findByExpiryDateBeforeAndType(Instant date, PaymentType type);
    List<PaymentEntity> findAllByTargetIdAndType(String targetId, PaymentType type);

}
