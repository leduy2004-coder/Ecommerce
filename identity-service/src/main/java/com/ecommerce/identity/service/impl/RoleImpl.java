package com.ecommerce.identity.service.impl;

import com.ecommerce.identity.dto.request.RoleRequest;
import com.ecommerce.identity.dto.response.RoleResponse;
import com.ecommerce.identity.entity.RoleEntity;
import com.ecommerce.identity.exception.AppException;
import com.ecommerce.identity.exception.ErrorCode;
import com.ecommerce.identity.repository.RoleRepository;
import com.ecommerce.identity.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleImpl implements RoleService {
    RoleRepository repository;
    ModelMapper modelMapper;

    @Override
    @Transactional
    public RoleResponse insert(RoleRequest roleDto) {
        return modelMapper.map(repository.save(modelMapper.map(roleDto, RoleEntity.class)), RoleResponse.class);
    }

    @Override
    public RoleResponse findByCode(String code) {
        RoleEntity role = repository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return modelMapper.map(role,RoleResponse.class);
    }

}
