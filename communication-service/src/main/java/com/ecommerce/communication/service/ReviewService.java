package com.ecommerce.communication.service;

import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.response.CommentGetResponse;
import com.ecommerce.communication.dto.response.ReviewGetResponse;
import com.ecommerce.communication.entity.ProductReviewEntity;
import com.ecommerce.communication.repository.ProductReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.Document;
import org.example.ProductGetReview;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ProductReviewRepository productReviewRepository;
    CommentService commentService;
    MongoTemplate mongoTemplate;

    public ReviewGetResponse getProductReviewAndComment(String productId) {
        ProductGetReview productGetReview = getProductReview(productId);

        PageResponse<CommentGetResponse> response = commentService.getCommentProduct(0, 10, productId);

        Map<Integer, Long> countAverageRatings = countAverageRatings();
        return ReviewGetResponse.builder()
                .countAverageRatings(countAverageRatings)
                .averageRating(productGetReview.getAverageRating())
                .totalComment(productGetReview.getTotalComment())
                .listComments(response)
                .build();
    }

    public ProductGetReview getProductReview(String productId) {
        ProductReviewEntity productReviewEntity = productReviewRepository.findById(productId).orElse(null);
        if (productReviewEntity == null) {
            return null;

        }
        return ProductGetReview.builder()
                .averageRating(productReviewEntity.getAverageRating())
                .totalComment(productReviewEntity.getTotalComment())
                .build();
    }

    public Map<Integer, Long> countAverageRatings() {
        // Tạo Aggregation để nhóm theo averageRating và đếm số lần xuất hiện
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("averageRating").count().as("count"),
                Aggregation.project("count").and("averageRating").previousOperation()
        );

        // Thực thi aggregation query
        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation,
                "product_review", // tên collection
                Document.class
        );

        // Tạo map và khởi tạo tất cả giá trị từ 1 đến 5 với giá trị mặc định là 0
        Map<Integer, Long> counts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            counts.put(i, 0L);
        }

        // Tạo map để lưu trữ kết quả
        for (Document doc : results) {
            Integer rating = doc.getInteger("averageRating"); // Lấy giá trị averageRating từ tài liệu
            Integer countValue = doc.getInteger("count");
            Long count = (countValue != null) ? countValue.longValue() : 0L;

            counts.put(rating, count);  // Lưu vào map
        }


        // Loại bỏ các phần tử có key là null trước khi trả về
        counts.entrySet().removeIf(entry -> entry.getKey() == null);

        return counts;

    }
}