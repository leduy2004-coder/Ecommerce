package com.ecommerce.identity.controller;


import com.ecommerce.identity.dto.request.UserRequest;
import com.ecommerce.identity.dto.response.ApiResponse;
import com.ecommerce.identity.dto.response.AuthenticationResponse;
import com.ecommerce.identity.dto.response.UserResponse;
import com.ecommerce.identity.entity.UserEntity;
import com.ecommerce.identity.service.UserService;
import com.ecommerce.identity.service.security.AuthenticationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {
    UserService userService;
    AuthenticationService service;
    ModelMapper modelMapper;

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(
            @RequestBody UserRequest request
    ) {
        return ApiResponse.<AuthenticationResponse>builder().result(service.register(request)).build();
    }


    @PostMapping("/add-user")
    public ApiResponse<UserResponse> addUser(
            @RequestBody UserRequest request
    ) {
        UserEntity user = userService.insert(request);
        return ApiResponse.<UserResponse>builder().result(modelMapper.map(user, UserResponse.class)).build();
    }


    @GetMapping("/get-all")
    public ApiResponse<List<UserResponse>> findAll() {
        List<UserResponse> list = userService.findAll();
        return ApiResponse.<List<UserResponse>>builder().result(list).build();
    }


    @GetMapping("/get-user")
    public ApiResponse<UserResponse> getUser(@RequestParam(value = "id") String id) {
        UserResponse userResponse = userService.findById(id);
        return ApiResponse.<UserResponse>builder().result(userResponse).build();
    }

    @DeleteMapping("/delete-user")
    public ApiResponse<Boolean> deleteUser(@RequestParam(value = "id") String id) {
        Boolean status = userService.delete(id);
        return ApiResponse.<Boolean>builder().result(status).build();
    }
}
