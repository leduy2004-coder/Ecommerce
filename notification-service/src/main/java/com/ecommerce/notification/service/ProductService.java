package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.request.NotifyRequest;
import com.ecommerce.notification.dto.response.NotifyResponse;
import com.ecommerce.notification.entity.ProductApproved;
import com.ecommerce.notification.entity.ProductExpired;
import com.ecommerce.notification.repository.ProductApprovedRepository;
import com.ecommerce.notification.repository.ProductExpiredRepository;
import com.ecommerce.notification.utility.NotifyStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductApprovedRepository productApprovedRepository;
    ProductExpiredRepository productExpiredRepository;
    ModelMapper modelMapper;

    public NotifyResponse saveNotify(NotifyRequest notifyRequest, NotifyStatus notifyStatus) {
        if(notifyStatus == NotifyStatus.APPROVED) {
            ProductApproved productApproved = productApprovedRepository.save(modelMapper.map(notifyRequest, ProductApproved.class));
            return modelMapper.map(productApproved, NotifyResponse.class);
        }else if(notifyStatus == NotifyStatus.EXPIRED) {
            ProductExpired productExpired = productExpiredRepository.save(modelMapper.map(notifyRequest, ProductExpired.class));
            return modelMapper.map(productExpired, NotifyResponse.class);
        }
        return null;
    }


}