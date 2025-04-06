package com.ecommerce.file.repository;


import com.ecommerce.file.entity.UserImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserImageEntity, String> {
    Optional<UserImageEntity> findAllByUserId(String userId);
}
