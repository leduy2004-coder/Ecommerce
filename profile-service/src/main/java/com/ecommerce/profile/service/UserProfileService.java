package com.ecommerce.profile.service;

import com.ecommerce.profile.dto.response.UserProfileResponse;
import com.ecommerce.profile.entity.UserProfile;
import com.ecommerce.profile.exception.AppException;
import com.ecommerce.profile.exception.ErrorCode;
import com.ecommerce.profile.repository.UserProfileRepository;
import org.example.ProfileCreationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    ModelMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.map(request, UserProfile.class);
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.map(userProfile, UserProfileResponse.class);
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile userProfile =
                userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        return userProfileMapper.map(userProfile, UserProfileResponse.class);
    }

    public UserProfileResponse getByUserId(String userId) {
        UserProfile userProfile =
                userProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userProfileMapper.map(userProfile, UserProfileResponse.class);
    }
}