package com.ecommerce.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.ecommerce.search.dto.PageResponse;
import com.ecommerce.search.dto.request.ProductSearchFilters;
import com.ecommerce.search.entity.ESProduct;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ESProductRepository {

    ElasticsearchClient client;

    public PageResponse<ESProduct> homepageSearch(String keyword, Pageable pageable, ProductSearchFilters filters) throws IOException {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("products")
                .query(q -> q
                        .bool(b -> {
                            BoolQuery.Builder boolBuilder = commonBoolQueryBuilder();
                            if (keyword != null && !keyword.isBlank()) {
                                boolBuilder.must(m -> m
                                        .bool(bq -> bq
                                                .should(sh -> sh
                                                        .match(mt -> mt
                                                                .field("name")
                                                                .query(keyword) // Tìm kiếm gần đúng với fuzziness
                                                                .fuzziness("AUTO") // Cho phép các sai sót nhẹ trong từ
                                                        )
                                                )
                                                .should(sh -> sh
                                                        .wildcard(w -> w
                                                                .field("name")
                                                                .value("*" + keyword + "*") // Tìm kiếm wildcard với từ khóa
                                                        )
                                                )
                                        )
                                );
                            }

                            if (!filters.getCategoryIds().isEmpty()) {
                                boolBuilder.filter(f -> f.terms(t ->
                                        t.field("categoryId").terms(ts ->
                                                ts.value(filters.getCategoryIds().stream().map(
                                                        FieldValue::of).toList()))));
                            }
                            if (filters.getMinPrice() != null) {
                                boolBuilder.filter(f -> f.range(m -> m.field("price").gte(JsonData.of(filters.getMinPrice()))));
                            }

                            if (filters.getMaxPrice() != null) {
                                boolBuilder.filter(f -> f.range(m -> m.field("price").lte(JsonData.of(filters.getMaxPrice()))));
                            }

                            if (filters.getMinRating() != null) {
                                boolBuilder.filter(f -> f.range(m -> m.field("averageRating").gte(JsonData.of(filters.getMinRating()))));
                            }


                            return boolBuilder;
                        })
                );
        return getEsProducts(pageable, searchBuilder);
    }

    public PageResponse<ESProduct> getShopProducts(String shopId, String categoryId, Pageable pageable) throws IOException {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("products")
                .query(q -> q
                        .bool(b -> {
                            BoolQuery.Builder boolBuilder = commonBoolQueryBuilder();
                            boolBuilder.must(m -> m.term(t ->
                                    t.field("shopId").value(shopId)));
                            if (categoryId != null && !categoryId.isEmpty()) {
                                boolBuilder.must(m -> m.term(t ->
                                        t.field("categoryId").value(categoryId)));
                            }
                            return boolBuilder;
                        })
                );
        return getEsProducts(pageable, searchBuilder);
    }

    private BoolQuery.Builder commonBoolQueryBuilder() {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        boolBuilder.must(m -> m.term(t ->
                t.field("visible").value(true)));
        boolBuilder.must(m -> m.terms(t ->
                t.field("status")
                        .terms(ts -> ts.value(
                                List.of(
                                        FieldValue.of(ProductStatus.ACTIVE.name())
                                )
                        ))
        ));
        return boolBuilder;
    }

    private PageResponse<ESProduct> getEsProducts(Pageable pageable, SearchRequest.Builder searchBuilder) throws IOException {
        // Lấy thông tin sort
        Sort sort = pageable.getSort();
        String sortBy = sort.get().toList().getFirst().getProperty();
        Sort.Direction direction = sort.get().toList().getFirst().getDirection();

        // Sắp xếp theo field chính
        searchBuilder.sort(s -> s.field(f ->
                f.field(sortBy)
                        .order(direction == Sort.Direction.DESC ? SortOrder.Desc : SortOrder.Asc)));

        // Ưu tiên sắp xếp theo rating nếu có
//        searchBuilder.sort(s -> s.field(f ->
//                f.field("averageRating")
//                        .order(SortOrder.Desc)));

        // Phân trang
        searchBuilder.from((int) pageable.getOffset())
                .size(pageable.getPageSize());

        // Gửi request
        SearchRequest rq = searchBuilder.build();
        SearchResponse<ObjectNode> response = client.search(rq, ObjectNode.class);

        // Log query để debug
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withQuery(Objects.requireNonNull(rq.query()));
        System.out.println(nativeQueryBuilder.getQuery());

        // Map kết quả
        List<ObjectNode> nodes = response.hits().hits().stream()
                .map(hit -> {
                    ObjectNode node = hit.source();
                    assert node != null;
                    node.put("id", hit.id());
                    return node;
                }).toList();

        List<ESProduct> products = nodes.stream()
                .map(this::toESProduct)
                .toList();

        // Tổng số phần tử
        long totalElements = response.hits().total() != null ? response.hits().total().value() : 0L;
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Trả về PageResponse
        return PageResponse.<ESProduct>builder()
                .currentPage(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .data(products)
                .build();
    }


    private ESProduct toESProduct(ObjectNode node) {
        ESProduct product = new ESProduct();
        product.setId(node.get("id").asText());
        product.setName(node.get("name").textValue());
        product.setPrice(node.get("price").longValue());
        product.setThumbnailUrl(node.get("thumbnailUrl").textValue());
        return product;
    }

}
