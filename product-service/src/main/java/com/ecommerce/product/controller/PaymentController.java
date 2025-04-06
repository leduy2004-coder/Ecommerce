package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ApiResponse;
import com.ecommerce.product.dto.request.PaymentRequest;
import com.ecommerce.product.dto.response.PaymentResponse;
import com.ecommerce.product.entity.PaymentEntity;
import com.ecommerce.product.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentController {

    @Value("${payment.vnPay.returnUrlAfterPayment}")
    String vnp_ReturnUrl;

    final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ApiResponse<PaymentResponse> pay(HttpServletRequest request) {
        return ApiResponse.<PaymentResponse>builder().result(paymentService.createVnPayPayment(request)).build();
    }

    @GetMapping("/vn-pay-callback")
    public ApiResponse<PaymentResponse> payCallbackHandler(HttpServletRequest request, HttpServletResponse response,
                                                           @RequestParam(value = "vnp_ResponseCode") String code,
                                                           @RequestParam(value = "vnp_Amount") String amount,
                                                           @RequestParam(value = "vnp_BankCode") String bankCode,
                                                           @RequestParam(value = "userId") String userId,
                                                           @RequestParam(value = "productId") String productId
    ) throws IOException {
        response.sendRedirect(vnp_ReturnUrl);
        PaymentResponse paymentDTO = paymentService.createPayment(PaymentRequest.builder()
                .productId(productId)
                .amount(Integer.parseInt(amount))
                .code(code)
                .bankCode(bankCode)
                .userId(userId)
                .build());
        return ApiResponse.<PaymentResponse>builder().result(paymentDTO).build();

    }
}
