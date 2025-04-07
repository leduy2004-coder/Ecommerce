package com.ecommerce.file.repository;


import com.ecommerce.file.entity.PostImageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<PostImageEntity, String> {
    List<PostImageEntity> findAllByPostId(String postId);
}
