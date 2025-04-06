package com.ecommerce.product.service;

import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.product.config.VNPAYConfig;
import com.ecommerce.product.dto.request.PaymentRequest;
import com.ecommerce.product.dto.response.PaymentResponse;
import com.ecommerce.product.entity.PaymentEntity;
import com.ecommerce.product.exception.AppException;
import com.ecommerce.product.exception.ErrorCode;
import com.ecommerce.product.repository.PaymentRepository;
import com.ecommerce.product.utility.GetInfo;
import com.ecommerce.product.utility.ProductStatus;
import com.ecommerce.product.utility.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ChannelNotify;
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
public class PaymentService {

    @Value("${payment.vnPay.returnUrl}")
    String vnp_ReturnUrl;

    final PaymentRepository paymentRepository;

    final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    final VNPAYConfig vnPayConfig;
    final ModelMapper modelMapper;
    final ProductService productService;

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        PaymentEntity paymentEntity = modelMapper.map(paymentRequest, PaymentEntity.class);
        paymentEntity.setStatus(true);
        int days = switch (paymentRequest.getAmount()) {
            case 30000 -> 30;
            case 60000 -> 60;
            case 90000 -> 90;
            default -> throw new IllegalArgumentException("Invalid amount. Supported amounts: 30000, 60000, 90000.");
        };
        paymentEntity.setExpiryDate(Instant.now().plus(days, ChronoUnit.DAYS));
        productService.updateStatusProduct(paymentRequest.getProductId(), ProductStatus.ACTIVE);

        return modelMapper.map(paymentRepository.save(paymentEntity), PaymentResponse.class);
    }

    public PaymentResponse createVnPayPayment(HttpServletRequest request) {
        String userId = GetInfo.getLoggedInUserName();
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String productId = request.getParameter("productId");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl + "?userId=" + userId+ "&productId=" + productId);

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
        Instant now = Instant.now();  // Lấy thời gian hiện tại theo UTC
        List<PaymentEntity> expiredPayments = paymentRepository.findByExpiryDateBefore(now);  // Truy vấn thanh toán hết hạn

        expiredPayments.forEach(payment -> {
            productService.updateStatusProduct(payment.getProductId(), ProductStatus.EXPIRED);
            updateStatusPayment(payment.getId(), false);

            String message = String.format("Sản phẩm có mã %s đã hết hạn, vui lòng gia hạn để sử dụng", payment.getProductId());

            NotificationEvent event = NotificationEvent.builder()
                    .channel(ChannelNotify.EXPIRY)
                    .subject(payment.getProductId())
                    .recipient(payment.getUserId())
                    .body(message)
                    .build();
            kafkaTemplate.send("payment-expired-product", event);  // Gửi sự kiện thanh toán hết hạn qua Kafka
        });
    }

    public void updateStatusPayment(String paymentId, Boolean status) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).orElse(null);
        if (paymentEntity == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }
        paymentEntity.setStatus(status);
        paymentRepository.save(paymentEntity);
    }
}
