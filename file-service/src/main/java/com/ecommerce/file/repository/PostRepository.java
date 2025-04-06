package com.ecommerce.file.repository;


import com.ecommerce.file.entity.PostImageEntity;
import com.ecommerce.file.entity.ProductImageEntity;
import org.apache.http.entity.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<PostImageEntity, String> {
    Optional<PostImageEntity> findAllByPostId(String postId);
}
