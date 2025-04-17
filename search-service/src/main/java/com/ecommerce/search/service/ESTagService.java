package com.ecommerce.search.service;

import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.event.dto.TagEvent;
import com.ecommerce.search.entity.ESProduct;
import com.ecommerce.search.entity.ESTag;
import com.ecommerce.search.repository.ESTagRepository;
import com.ecommerce.search.repository.ESTagRepository_I;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class ESTagService {

    ESTagRepository estagRepository;
    ESTagRepository_I estagRepository_I;
    ModelMapper modelMapper;

    public List<String> searchTagsByName(String keyword) throws IOException {
        return estagRepository.searchTagsByName(keyword);
    }

    public void indexOrUpdateTag(TagEvent event) {
        ESTag esProduct = modelMapper.map(event, ESTag.class);
        estagRepository_I.save(esProduct);
        log.info("Product {} indexed/updated in Elasticsearch.", esProduct.getId());

    }
}
  