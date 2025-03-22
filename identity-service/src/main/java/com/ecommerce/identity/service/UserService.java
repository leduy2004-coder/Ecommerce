package com.ecommerce.identity.service;


import com.ecommerce.identity.dto.request.UserRequest;
import com.ecommerce.identity.dto.response.UserResponse;
import com.ecommerce.identity.entity.UserEntity;

import java.math.BigInteger;
import java.util.List;

public interface UserService {
    UserEntity insert(UserRequest userDto);
    Boolean delete(String id);
    UserResponse findById(String id);
    List<UserResponse> findAll();
    UserResponse findByUsername(String userName);
    UserResponse updateUser(UserRequest userRequest);



}
