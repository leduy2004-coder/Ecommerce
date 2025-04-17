package com.ecommerce.search.repository;

import com.ecommerce.search.entity.ESTag;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESTagRepository_I extends ElasticsearchRepository<ESTag, String> {

}