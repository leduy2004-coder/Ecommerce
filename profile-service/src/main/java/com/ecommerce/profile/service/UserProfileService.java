package com.ecommerce.profile.service;

import com.ecommerce.profile.entity.UserProfile;
import com.ecommerce.profile.exception.AppException;
import com.ecommerce.profile.exception.ErrorCode;
import com.ecommerce.profile.repository.UserProfileRepository;
import com.ecommerce.profile.repository.httpClient.FileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {

    FileClient fileClient;
    UserProfileRepository userProfileRepository;
    ModelMapper userProfileMapper;

    public ProfileCreationResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.map(request, UserProfile.class);
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.map(userProfile, ProfileCreationResponse.class);
    }

    public ProfileCreationResponse getProfile(String id) {
        UserProfile userProfile =
                userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        return userProfileMapper.map(userProfile, ProfileCreationResponse.class);
    }

    public ProfileGetResponse getByUserId(String userId) {
        UserProfile userProfile =
                userProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        ProfileGetResponse profileGetResponse = userProfileMapper.map(userProfile, ProfileGetResponse.class);
        List<CloudinaryResponse> avatars = fileClient.getImageProduct(userId, ImageType.AVATAR).getResult();

        if (avatars != null && !avatars.isEmpty()) {
            profileGetResponse.setAvatar(avatars.getFirst().getUrl());
        } else {
            profileGetResponse.setAvatar(null);
        }

        return profileGetResponse;
    }
}