package com.ecommerce.search.service;

import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.search.dto.PageResponse;
import com.ecommerce.search.dto.request.ProductSearchFilters;
import com.ecommerce.search.dto.response.ProductSearchResponse;
import com.ecommerce.search.entity.ESProduct;
import com.ecommerce.search.exception.AppException;
import com.ecommerce.search.exception.ErrorCode;
import com.ecommerce.search.repository.ESProductRepository;
import com.ecommerce.search.repository.ESProductRepository_I;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ESProductService {

    ESProductRepository productRepository;
    ESProductRepository_I productRepository_I;

    ModelMapper modelMapper;

    public PageResponse<ProductSearchResponse> homepageSearch(String keyword,
                                                              String sortBy,
                                                              String order,
                                                              int page, int limit,
                                                              ProductSearchFilters filters){
        switch (sortBy){
            case "relevance":
                sortBy = "_score";
                break;
            case "newest":
                sortBy = "createdDate";
                break;
            case "sales":
                sortBy = "sold";
                break;
            case "price":
                sortBy = "price";
                break;
            default:
                throw new AppException(ErrorCode.INVALID_SORT_BY);
        }
        Sort sort = Sort.by(
                order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        PageResponse<ESProduct> pageRes;
        try {
            pageRes = productRepository.homepageSearch(keyword, pageable, filters);
        } catch (IOException e) {
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
        return pageRes.map(es -> modelMapper.map(es, ProductSearchResponse.class));
    }

    public void indexOrUpdateProduct(ProductEvent event) {
        if (event.isDeleted()) {
            productRepository_I.deleteById(event.getId());
            log.info("Product {} deleted from Elasticsearch.", event.getId());
        } else {
            ESProduct esProduct = modelMapper.map(event, ESProduct.class);
            productRepository_I.save(esProduct);
            log.info("Product {} indexed/updated in Elasticsearch.", esProduct.getId());
        }
    }
}
