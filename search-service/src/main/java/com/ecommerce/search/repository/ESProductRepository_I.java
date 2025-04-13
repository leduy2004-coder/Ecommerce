package com.ecommerce.search.repository;

import com.ecommerce.search.entity.ESProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESProductRepository_I extends ElasticsearchRepository<ESProduct, String> {

}