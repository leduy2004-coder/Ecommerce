package com.ecommerce.file.repository;


import com.ecommerce.file.entity.PostImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<PostImageEntity, String> {
//     Optional<FileEntity> findByCode(String code);
}
