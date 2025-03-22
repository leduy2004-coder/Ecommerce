package com.ecommerce.identity.service;


import com.ecommerce.identity.dto.request.RoleRequest;
import com.ecommerce.identity.dto.response.RoleResponse;

public interface RoleService {
    RoleResponse insert(RoleRequest roleDto);
    RoleResponse findByCode(String code);
}
