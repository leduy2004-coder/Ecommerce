package com.ecommerce.post.repository;


import com.ecommerce.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<PostEntity, String> {
    Page<PostEntity> findAllByUserId(String userId, Pageable pageable);
}