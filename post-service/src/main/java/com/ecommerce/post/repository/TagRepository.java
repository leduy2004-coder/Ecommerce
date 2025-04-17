package com.ecommerce.post.repository;


import com.ecommerce.post.entity.TagEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TagRepository extends MongoRepository<TagEntity, String> {
    boolean existsByName(String name);
    TagEntity findByName(String name);

    List<TagEntity> findTop10ByOrderByCountDesc();
}