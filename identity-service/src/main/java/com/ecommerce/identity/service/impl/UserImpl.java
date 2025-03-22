package com.ecommerce.identity.service.impl;


import com.ecommerce.identity.dto.request.UserRequest;
import com.ecommerce.identity.dto.response.UserResponse;
import com.ecommerce.identity.entity.RoleEntity;
import com.ecommerce.identity.entity.UserEntity;
import com.ecommerce.identity.exception.AppException;
import com.ecommerce.identity.exception.ErrorCode;
import com.ecommerce.identity.repository.UserRepository;
import com.ecommerce.identity.service.RoleService;
import com.ecommerce.identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserImpl implements UserService {
   UserRepository userRepository;
   ModelMapper modelMapper;
   RoleService roleService;
   PasswordEncoder passwordEncoder;

    @Override
    public UserEntity insert(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent())
            throw new AppException(ErrorCode.USER_EXISTED);

        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);

        if (userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
            List<RoleEntity> roles = userRequest.getRoles().stream()
                    .map(role ->
                            modelMapper.map(roleService.findByCode(role.getCode()), RoleEntity.class))
                    .collect(Collectors.toList());
            userEntity.setRoles(roles);
        }
        if (userRequest.getAuthType().name().equalsIgnoreCase("LOCAL"))
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setStatus(true);
        return userRepository.save(userEntity);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Boolean delete(String id) {
        try {
            Optional<UserEntity> entity = userRepository.findById(id);
            if (entity.isPresent()) {
                userRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserResponse findById(String id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);
        if (user == null) {
            return null;
        }
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        return userResponse;
    }



    @Override
    public UserResponse findByUsername(String userName) {
        UserEntity user = userRepository.findByUsername(userName)
                .orElse(null);
        if (user == null) {
            return null;
        }
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return userResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponse> findAll() {
        log.info("In method in admin");
        List<UserEntity> list = userRepository.findAll();
        return mapUserEntitiesToResponses(list);
    }


    @Override
    public UserResponse updateUser(UserRequest userRequest) {
        UserEntity userEntity = userRepository.findById(userRequest.getId()).orElseThrow();
        userEntity.setStatus(userRequest.getStatus());
        if (userRequest.getUsername() != null) {
            userEntity.setUsername(userRequest.getUsername());
        }
        if (userRequest.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        UserEntity user = userRepository.save(userEntity);
        return modelMapper.map(user, UserResponse.class);
    }


    public List<UserResponse> mapUserEntitiesToResponses(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(userEntity -> {
                    return modelMapper.map(userEntity, UserResponse.class);
                })
                .toList();
    }


}