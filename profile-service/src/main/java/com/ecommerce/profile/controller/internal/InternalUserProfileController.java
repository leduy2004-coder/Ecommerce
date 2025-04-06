package com.ecommerce.profile.controller.internal;


import com.ecommerce.profile.dto.ApiResponse;
import com.ecommerce.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ProfileCreationRequest;
import org.example.ProfileCreationResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    ApiResponse<ProfileCreationResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<ProfileCreationResponse>builder().result(userProfileService.createProfile(request)).build();
    }

    @GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileCreationResponse> getProfile(@PathVariable String userId) {
        return ApiResponse.<ProfileCreationResponse>builder()
                .result(userProfileService.getByUserId(userId))
                .build();
    }
}