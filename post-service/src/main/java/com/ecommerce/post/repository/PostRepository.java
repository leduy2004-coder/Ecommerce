package com.ecommerce.post.repository;


import com.ecommerce.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<PostEntity, String> {
    Page<PostEntity> findAllByUserId(String userId, Pageable pageable);

    @Query("{ 'hashTags': { $in: ?0 } }")
    Page<PostEntity> findAllByHashTags(List<String> tags, Pageable pageable);
}