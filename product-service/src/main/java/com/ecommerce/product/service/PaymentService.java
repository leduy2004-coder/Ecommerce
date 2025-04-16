package com.ecommerce.product.service;

import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.product.config.VNPAYConfig;
import com.ecommerce.product.dto.request.PaymentRequest;
import com.ecommerce.product.dto.response.PaymentResponse;
import com.ecommerce.product.entity.BannerEntity;
import com.ecommerce.product.entity.PaymentEntity;
import com.ecommerce.product.entity.ProductEntity;
import com.ecommerce.product.exception.AppException;
import com.ecommerce.product.exception.ErrorCode;
import com.ecommerce.product.repository.BannerRepository;
import com.ecommerce.product.repository.PaymentRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.httpClient.CommunicationClient;
import com.ecommerce.product.repository.httpClient.FileClient;
import com.ecommerce.product.utility.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.ChannelNotify;
import org.example.ImageType;
import org.example.ProductGetReview;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class PaymentService {

    @Value("${payment.vnPay.returnUrl}")
    String vnp_ReturnUrl;

    final PaymentRepository paymentRepository;

    final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    final KafkaTemplate<String, ProductEvent> productKafkaTemplate;

    final VNPAYConfig vnPayConfig;
    final FileClient fileClient;
    final ModelMapper modelMapper;
    final ProductRepository productRepository;
    final CommunicationClient communicationClient;
    final BannerRepository bannerRepository;


    public PaymentResponse createPaymentProduct(PaymentRequest paymentRequest) {
        String userId = GetInfo.getLoggedInUserName();
        var optionalProduct = productRepository.findByIdAndUserId(paymentRequest.getTargetId(), userId);
        if (optionalProduct.isEmpty())
            throw new AppException(ErrorCode.PRODUCT_USER_NOT_EXISTED);
        //set inactive all payment of product
        inactivatePayment(paymentRequest.getTargetId());

        PaymentEntity paymentEntity = modelMapper.map(paymentRequest, PaymentEntity.class);
        paymentEntity.setStatus(PaymentStatus.ACTIVE);
        int days = switch (paymentRequest.getAmount()) {
            case 3000000 -> 30;
            case 6000000 -> 60;
            case 9000000 -> 90;
            default -> throw new IllegalArgumentException("Invalid amount. Supported amounts: 30000, 60000, 90000.");
        };
        paymentEntity.setExpiryDate(Instant.now().plus(days, ChronoUnit.DAYS));
        paymentEntity.setUserId(userId);
        paymentEntity.setType(PaymentType.PRODUCT);
        PaymentEntity savedPaymentEntity = paymentRepository.save(paymentEntity);

        updateStatusProduct(savedPaymentEntity.getTargetId(), ProductStatus.ACTIVE);

        log.info("Payment product created successfully");
        return modelMapper.map(savedPaymentEntity, PaymentResponse.class);
    }

    public PaymentResponse createPaymentBanner(PaymentRequest paymentRequest) {
        String userId = GetInfo.getLoggedInUserName();

        PaymentEntity paymentEntity = modelMapper.map(paymentRequest, PaymentEntity.class);
        paymentEntity.setType(PaymentType.BANNER);
        paymentEntity.setUserId(userId);
        PaymentEntity savedPaymentEntity = paymentRepository.save(paymentEntity);

        updateStatusBanner(savedPaymentEntity.getTargetId(), BannerStatus.PAYMENT);

        log.info("Payment banner created successfully");
        return modelMapper.map(savedPaymentEntity, PaymentResponse.class);
    }

    public PaymentResponse createVnPayPayment(HttpServletRequest request) {
        long amount = (long) Integer.parseInt(request.getParameter("amount")) * 100L;

        String bankCode = request.getParameter("bankCode");
        String id = request.getParameter("targetId");
        String type = request.getParameter("type");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl + "&targetId=" + id + "&type=" + type);

        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Scheduled(cron = "0 0 0 * * ?")  // Chạy mỗi ngày lúc nửa đêm
    public void checkPaymentsExpiry() {
        log.info("Checking payments expiry");
        Instant now = Instant.now();  // Lấy thời gian hiện tại theo UTC
        List<PaymentEntity> expiredPayments = paymentRepository.findByExpiryDateBeforeAndType(now, PaymentType.PRODUCT);  // Truy vấn thanh toán hết hạn
        log.info("Expired payments: {}", expiredPayments);

        expiredPayments.forEach(payment -> {
            updateStatusProduct(payment.getTargetId(), ProductStatus.EXPIRED);
            updateStatusPayment(payment.getId(), PaymentStatus.EXPIRED);

            String message = String.format("Sản phẩm có mã %s đã hết hạn, vui lòng gia hạn để sử dụng", payment.getTargetId());

            NotificationEvent event = NotificationEvent.builder()
                    .channel(ChannelNotify.EXPIRY)
                    .subject(payment.getTargetId())
                    .recipient(payment.getUserId())
                    .body(message)
                    .build();
            kafkaTemplate.send("payment-expired-product", event);  // Gửi sự kiện thanh toán hết hạn qua Kafka
        });
    }

    public void updateStatusPayment(String paymentId, PaymentStatus status) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).orElse(null);
        if (paymentEntity == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }
        paymentEntity.setStatus(status);
        paymentRepository.save(paymentEntity);
    }

    public void inactivatePayment(String productId) {
        List<PaymentEntity> list = paymentRepository.findAllByTargetIdAndType(productId, PaymentType.PRODUCT);
        if (list.isEmpty()) {
            return;
        }
        list.forEach(paymentEntity -> {
            paymentEntity.setStatus(PaymentStatus.INACTIVE);
            paymentRepository.save(paymentEntity);
        });
    }

    public void updateStatusProduct(String productId, ProductStatus status) {
        ProductEntity productEntity = productRepository.findById(productId).orElse(null);
        if (productEntity == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        productEntity.setStatus(status);
        ProductEntity productSave = productRepository.save(productEntity);

        //sync data product to elastic search
        ProductEvent productEvent = modelMapper.map(productSave, ProductEvent.class);
        syncProductToElastic(productEvent);
    }
    public void updateStatusBanner(String bannerId, BannerStatus status) {
        BannerEntity bannerEntity = bannerRepository.findById(bannerId).orElse(null);
        if (bannerEntity == null) {
            throw new AppException(ErrorCode.BANNER_NOT_EXISTED);
        }
        bannerEntity.setStatus(status);
        bannerRepository.save(bannerEntity);
    }
    public void syncProductToElastic(ProductEvent product) {
        var img = fileClient.getImage(product.getId(), ImageType.PRODUCT);
        product.setThumbnailUrl(img.getResult().getFirst().getUrl());
        ProductGetReview productGetReview = communicationClient.getImageProduct(product.getId()).getResult();
        product.setTotalComment(productGetReview.getTotalComment());
        product.setAverageRating(productGetReview.getAverageRating());
        productKafkaTemplate.send("product-sync", product);
    }
}
