package com.ecommerce.file.repository;


import com.ecommerce.file.entity.PostImageEntity;
import com.ecommerce.file.entity.UserImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserImageEntity, String> {
//     Optional<FileEntity> findByCode(String code);
}
