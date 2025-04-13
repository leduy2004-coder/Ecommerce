package com.ecommerce.communication.service;

import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.response.CommentGetResponse;
import com.ecommerce.communication.dto.response.ReviewGetResponse;
import com.ecommerce.communication.entity.ProductReviewEntity;
import com.ecommerce.communication.repository.ProductReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ProductGetReview;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ProductReviewRepository productReviewRepository;
    CommentService commentService;

    public ReviewGetResponse getProductReviewAndComment(String productId) {
        ProductGetReview productGetReview = getProductReview(productId);

        PageResponse<CommentGetResponse> response = commentService.getCommentProduct(0, 10, productId);

        return ReviewGetResponse.builder()
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
}