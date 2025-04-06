package com.ecommerce.notification.repository;


import com.ecommerce.notification.entity.ProductApproved;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductApprovedRepository extends MongoRepository<ProductApproved, String> {

}
