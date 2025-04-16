package com.ecommerce.product.service;


import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.product.dto.PageResponse;
import com.ecommerce.product.dto.request.BannerCreateRequest;
import com.ecommerce.product.dto.response.BannerGetActiveResponse;
import com.ecommerce.product.dto.response.BannerGetResponse;
import com.ecommerce.product.dto.response.ProductGetResponse;
import com.ecommerce.product.entity.BannerEntity;
import com.ecommerce.product.entity.ProductEntity;
import com.ecommerce.product.exception.AppException;
import com.ecommerce.product.exception.ErrorCode;
import com.ecommerce.product.repository.BannerRepository;
import com.ecommerce.product.repository.httpClient.FileClient;
import com.ecommerce.product.utility.BannerStatus;
import com.ecommerce.product.utility.GetInfo;
import com.ecommerce.product.utility.ProductStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ChannelNotify;
import org.example.CloudinaryResponse;
import org.example.FileDeleteRequest;
import org.example.ImageType;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerService {
    BannerRepository bannerRepository;
    ModelMapper modelMapper;
    FileClient fileClient;
    KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    KafkaTemplate<String, ProductEvent> productKafkaTemplate;
    PaymentService paymentService;

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<BannerGetResponse> getAllBanner(int page, int size) {
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<BannerEntity> pageData = bannerRepository.findAll(pageable); // có phân trang

        List<BannerGetResponse> bannerResponses = pageData.getContent().stream()
                .map(bannerEntity -> {
                    BannerGetResponse response = modelMapper.map(bannerEntity, BannerGetResponse.class);
                    response.setImgUrl(
                            fileClient.getImage(bannerEntity.getId(), ImageType.BANNER).getResult().getFirst()
                    );
                    return response;
                })
                .collect(Collectors.toList());

        return PageResponse.<BannerGetResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(bannerResponses)
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<BannerGetResponse> getBannersByUserId(String userId) {
        List<BannerEntity> banners = bannerRepository.findAllByUserId(userId);

        return banners.stream()
                .map(bannerEntity -> {
                    BannerGetResponse bannerGetResponse = modelMapper.map(bannerEntity, BannerGetResponse.class);
                    bannerGetResponse.setImgUrl(fileClient.getImage(bannerEntity.getId(), ImageType.BANNER)
                            .getResult()
                            .getFirst());
                    return bannerGetResponse;
                })
                .collect(Collectors.toList());
    }
    public List<BannerGetResponse> getMyBanners() {
        List<BannerEntity> banners = bannerRepository.findAllByUserId(GetInfo.getLoggedInUserName());

        return banners.stream()
                .map(bannerEntity -> {
                    com.ecommerce.product.dto.response.BannerGetResponse bannerGetResponse = modelMapper.map(bannerEntity, com.ecommerce.product.dto.response.BannerGetResponse.class);
                    bannerGetResponse.setImgUrl(fileClient.getImage(bannerEntity.getId(), ImageType.BANNER)
                            .getResult()
                            .getFirst());
                    return bannerGetResponse;
                })
                .collect(Collectors.toList());
    }
    public List<BannerGetActiveResponse> getActiveOrPaymentBanners() {
        List<BannerEntity> banners = bannerRepository.findActiveOrPaymentBanners();
        if (banners == null) {
            return null;
        }
        return banners.stream()
                .map(bannerEntity -> BannerGetActiveResponse.builder()
                        .dateEnd(bannerEntity.getDateEnd())
                        .dateStart(bannerEntity.getDateStart())
                        .build())
                .collect(Collectors.toList());
    }
    public BannerGetResponse getBannerByStatus(BannerStatus status) {
        var banner = bannerRepository.findByStatus(status).orElse(null);

        if (banner == null) {
            return null;
        }
        BannerGetResponse bannerGetResponse = modelMapper.map(banner, BannerGetResponse.class);
        bannerGetResponse.setImgUrl(fileClient.getImage(banner.getId(), ImageType.BANNER).getResult().getFirst());
        return bannerGetResponse;
    }

    public BannerGetResponse createBanner(BannerCreateRequest request, MultipartFile file) {
        if (!checkDateStartBanner(request.getDateStart(), request.getDateEnd())){
            throw new AppException(ErrorCode.BANNER_DATE_NOT_VALID);
        }
        BannerEntity bannerEntity = BannerEntity.builder()
                .userId(GetInfo.getLoggedInUserName())
                .dateEnd(request.getDateEnd())
                .dateStart(request.getDateStart())
                .status(BannerStatus.PENDING)
                .build();
        BannerEntity bannerSave = bannerRepository.save(bannerEntity);
        String bannerId = bannerSave.getId();

        var response = fileClient.uploadBanner(file, bannerId);
        CloudinaryResponse imgUrl = response.getResult();

        BannerGetResponse bannerGetResponse = modelMapper.map(bannerSave, BannerGetResponse.class);
        bannerGetResponse.setImgUrl(imgUrl);

        return bannerGetResponse;
    }
    public boolean checkDateStartBanner(Date dateStart, Date dateEnd) {
        List<BannerEntity> list = bannerRepository.findActiveOrPaymentBannerOverlap(dateStart, dateEnd);
        return list.isEmpty();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public BannerStatus acceptBanner(String bannerId) {
        BannerEntity bannerEntity = bannerRepository.findById(bannerId).orElse(null);
        if (bannerEntity == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        bannerEntity.setStatus(BannerStatus.APPROVED);
        bannerRepository.save(bannerEntity);

        String message = String.format("Banner có mã %s đã được duyệt. Vui lòng thanh toán để quảng bá.", bannerEntity.getId());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel(ChannelNotify.APPROVED_BANNER)
                .recipient(bannerEntity.getUserId())
                .subject(bannerId)
                .body(message)
                .build();

        // Publish message to kafka
        kafkaTemplate.send("notification-approved-product", notificationEvent);

        return bannerEntity.getStatus();
    }

    public void updateStatusBanner(String bannerId, BannerStatus status) {
        BannerEntity bannerEntity = bannerRepository.findById(bannerId).orElse(null);
        if (bannerEntity == null) {
            throw new AppException(ErrorCode.BANNER_NOT_EXISTED);
        }
        bannerEntity.setStatus(status);
        bannerRepository.save(bannerEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteBanner(String bannerId) {
        try {
            updateStatusBanner(bannerId, BannerStatus.INACTIVE);

            fileClient.deleteImageProduct(FileDeleteRequest.builder().id(bannerId).type(ImageType.BANNER).build());

            return true;
        } catch (Exception e) {
            return false;
        }
    }



}