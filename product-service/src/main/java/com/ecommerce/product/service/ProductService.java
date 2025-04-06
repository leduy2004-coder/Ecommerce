package com.ecommerce.product.service;


import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.product.dto.request.ProductCreateRequest;
import com.ecommerce.product.dto.response.ProductCreateResponse;
import com.ecommerce.product.entity.ProductEntity;
import com.ecommerce.product.exception.AppException;
import com.ecommerce.product.exception.ErrorCode;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.httpClient.FileClient;
import com.ecommerce.product.utility.ProductStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ChannelNotify;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ModelMapper modelMapper;
    FileClient fileClient;
    KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public ProductCreateResponse createProduct(ProductCreateRequest request, List<MultipartFile> files) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProductEntity product = ProductEntity.builder()
                .affiliateLink(request.getAffiliateLink())
                .price(request.getPrice())
                .description(request.getDescription())
                .userId(authentication.getName())
                .status(ProductStatus.PENDING)
                .build();
        product = productRepository.save(product);
        String productId = product.getId();

        List<String> imgUrl = new ArrayList<>();

        for (MultipartFile file : files) {
            var response = fileClient.uploadMediaProduct(file, productId);
            imgUrl.add(response.getResult().getUrl());
        }

        ProductCreateResponse productCreateResponse = modelMapper.map(product, ProductCreateResponse.class);
        productCreateResponse.setImgUrl(imgUrl);

        return productCreateResponse;
    }


//    public PageResponse<PostResponse> getMyPosts(int page, int size){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userId = authentication.getName();
//
//        Sort sort = Sort.by("createdDate").descending();
//        Pageable pageable = PageRequest.of(page - 1, size, sort);
//        var pageData = postRepository.findAllByUserId(userId, pageable);
//
//        List<PostResponse> postList = pageData.getContent().stream().map(post -> {
//            var postResponse = modelMapper.map(post, PostResponse.class);
//            postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
//            return postResponse;
//        }).toList();
//
//        return PageResponse.<PostResponse>builder()
//                .currentPage(page)
//                .pageSize(pageData.getSize())
//                .totalPages(pageData.getTotalPages())
//                .totalElements(pageData.getTotalElements())
//                .data(postList)
//                .build();
//    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductStatus acceptProduct(String productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElse(null);
        if (productEntity == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        productEntity.setStatus(ProductStatus.APPROVED);
        productRepository.save(productEntity);

        String message = String.format("Sản phẩm có mã %s đã được duyệt. Vui lòng thanh toán để sử dụng.", productId);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel(ChannelNotify.APPROVED_PRODUCT)
                .recipient(productEntity.getUserId())
                .subject(productId)
                .body(message)
                .build();

        // Publish message to kafka
        kafkaTemplate.send("notification-approved-product", notificationEvent);

        return productEntity.getStatus();
    }

    public void updateStatusProduct(String productId, ProductStatus status) {
        ProductEntity productEntity = productRepository.findById(productId).orElse(null);
        if (productEntity == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        productEntity.setStatus(status);
        productRepository.save(productEntity);
    }
}