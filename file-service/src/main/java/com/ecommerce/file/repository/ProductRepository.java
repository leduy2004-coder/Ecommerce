package com.ecommerce.file.repository;


import com.ecommerce.file.entity.PostImageEntity;
import com.ecommerce.file.entity.ProductImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<ProductImageEntity, String> {
//     Optional<FileEntity> findByCode(String code);
}
