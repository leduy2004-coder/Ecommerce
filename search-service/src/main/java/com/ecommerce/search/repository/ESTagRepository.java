package com.ecommerce.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecommerce.search.entity.ESProduct;
import com.ecommerce.search.entity.ESTag;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class ESTagRepository {

    ElasticsearchClient elasticsearchClient;

    public List<String> searchTagsByName(String keyword) throws IOException {
        if (keyword == null || keyword.isBlank()) return List.of();

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("tags")
                .query(q -> q.bool(b -> b
                        .should(sh -> sh.match(m -> m
                                .field("name")
                                .query(keyword)
                                .fuzziness("AUTO")
                        ))
                        .should(sh -> sh.wildcard(w -> w
                                .field("name")
                                .value("*" + keyword.toLowerCase() + "*")
                        ))
                ))
                .size(10)
                .build();

        // Trả về ObjectNode để kiểm tra dữ liệu trước
        SearchResponse<ObjectNode> response = elasticsearchClient.search(searchRequest, ObjectNode.class);

        // In ra để kiểm tra dữ liệu trả về
        response.hits().hits().forEach(hit -> {
            System.out.println("Source: " + hit.source());
        });

        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(node -> node.get("name").asText()) // Trích xuất tên tag
                .distinct()
                .collect(Collectors.toList());
    }

}
