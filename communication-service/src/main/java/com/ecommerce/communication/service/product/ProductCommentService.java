package com.ecommerce.communication.service.product;

import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.request.CommentProductCreateRequest;
import com.ecommerce.communication.dto.response.CommentProductCreateResponse;
import com.ecommerce.communication.dto.response.CommentProductGetResponse;
import com.ecommerce.communication.entity.ProductReviewEntity;
import com.ecommerce.communication.entity.ProductCommentEntity;
import com.ecommerce.communication.repository.ProductReviewRepository;
import com.ecommerce.communication.repository.ProductCommentRepository;
import com.ecommerce.communication.repository.httpClient.ProfileClient;
import com.ecommerce.communication.service.DateTimeFormatter;
import com.ecommerce.communication.utility.GetInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.ProfileGetResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductCommentService {

    ProductCommentRepository productCommentRepository;
    ProductReviewRepository productReviewRepository;
    ModelMapper modelMapper;
    DateTimeFormatter dateTimeFormatter;
    ProfileClient profileClient;

    public CommentProductCreateResponse saveProductReview(CommentProductCreateRequest commentCreateRequest) {
        String userId = GetInfo.getLoggedInUserName();

        ProductCommentEntity productComment = modelMapper.map(commentCreateRequest, ProductCommentEntity.class);
        productComment.setUserId(userId);
        ProductCommentEntity productCommentSave = productCommentRepository.save(productComment);

        ProductReviewEntity productReviewEntity = productReviewRepository.findById(commentCreateRequest.getProductId()).orElse(null);

        // Nếu không tìm thấy ProductRatingEntity
        if (productReviewEntity == null) {
            productReviewEntity = ProductReviewEntity.builder().averageRating(0).totalComment(0).productId(commentCreateRequest.getProductId()).build();
        }

        // Cập nhật tổng số bình luận
        productReviewEntity.setTotalComment(productReviewEntity.getTotalComment() + 1);

        // Tính lại điểm trung bình mới
        int currentTotalComments = productReviewEntity.getTotalComment();
        int currentAverageRating = productReviewEntity.getAverageRating();
        int newRating = commentCreateRequest.getRating();

        // Công thức tính trung bình mới
        int newAverageRating = (currentAverageRating * (currentTotalComments - 1) + newRating) / currentTotalComments;

        // Cập nhật điểm trung bình
        productReviewEntity.setAverageRating(newAverageRating);

        // Lưu lại thông tin ProductRatingEntity đã được cập nhật
        productReviewRepository.save(productReviewEntity);

        log.info("Comment successfully saved {}", productCommentSave.getComment());

        return CommentProductCreateResponse.builder()
                .productId(productReviewEntity.getProductId())
                .rating(commentCreateRequest.getRating())
                .comment(commentCreateRequest.getComment())
                .created(dateTimeFormatter.format(productCommentSave.getCreatedDate()))
                .totalComment(productReviewEntity.getTotalComment())
                .averageRating(newAverageRating)
                .id(productCommentSave.getId())
                .build();
    }

    public PageResponse<CommentProductGetResponse> getCommentProduct(int page, int size, String productId) {
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        var listComments = productCommentRepository.findAllByProductId(productId,pageable);
        List<CommentProductGetResponse> postList = listComments.getContent().stream().map(comment -> {
            var postResponse = modelMapper.map(comment, CommentProductGetResponse.class);
            postResponse.setCreated(dateTimeFormatter.format(comment.getCreatedDate()));
            ProfileGetResponse profile = profileClient.getProfile(comment.getUserId()).getResult();
            postResponse.setUser(profile);
            return postResponse;
        }).toList();

        return PageResponse.<CommentProductGetResponse>builder()
                .currentPage(1)
                .pageSize(listComments.getSize())
                .totalPages(listComments.getTotalPages())
                .totalElements(listComments.getTotalElements())
                .data(postList)
                .build();
    }

}
