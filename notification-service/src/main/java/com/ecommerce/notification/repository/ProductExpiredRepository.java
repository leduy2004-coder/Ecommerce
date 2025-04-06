package com.ecommerce.notification.repository;


import com.ecommerce.notification.entity.ProductExpired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductExpiredRepository extends MongoRepository<ProductExpired, String> {

}
