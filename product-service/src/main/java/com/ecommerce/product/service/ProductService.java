package com.ecommerce.product.service;


import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.product.dto.PageResponse;
import com.ecommerce.product.dto.request.ProductCreateRequest;
import com.ecommerce.product.dto.response.ProductCreateResponse;
import com.ecommerce.product.dto.response.ProductGetResponse;
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
import org.example.CloudinaryResponse;
import org.example.FileDeleteRequest;
import org.example.ImageType;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    PaymentService paymentService;

    public ProductGetResponse getProductById(String productId) {
        ProductEntity product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        ProductGetResponse productGetResponse = modelMapper.map(product, ProductGetResponse.class);
        productGetResponse.setImgUrl(fileClient.getImageProduct(productId, ImageType.PRODUCT).getResult());
        return productGetResponse;
    }

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

        List<CloudinaryResponse> imgUrl = new ArrayList<>();

        for (MultipartFile file : files) {
            var response = fileClient.uploadMediaProduct(file, productId);
            imgUrl.add(response.getResult());
        }

        ProductCreateResponse productCreateResponse = modelMapper.map(product, ProductCreateResponse.class);
        productCreateResponse.setImgUrl(imgUrl);

        return productCreateResponse;
    }

    public PageResponse<ProductGetResponse> getProductsByUserId(int page, int size, String userId){
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = productRepository.findAllByUserId(userId, pageable);

        List<ProductGetResponse> productList = pageData.getContent().stream().map(post -> {
            var productResponse = modelMapper.map(post, ProductGetResponse.class);
            productResponse.setImgUrl(fileClient.getImageProduct(productResponse.getId(), ImageType.PRODUCT).getResult());
            return productResponse;
        }).toList();

        return PageResponse.<ProductGetResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(productList)
                .build();
    }

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

    public Boolean deleteProductOfUser(String productId) {
        try {
            updateStatusProduct(productId, ProductStatus.INACTIVE);

            paymentService.inactivatePayment(productId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteProductOfAdmin(String productId) {
        try {
            productRepository.deleteById(productId);

            paymentService.inactivatePayment(productId);

            fileClient.deleteImageProduct(FileDeleteRequest.builder().id(productId).type(ImageType.PRODUCT).build());
            return true;
        }catch (Exception e){
            return false;
        }
    }
}