package com.ecommerce.profile.controller;

import com.ecommerce.profile.service.UserProfileService;
import org.example.ProfileCreationResponse;
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/users/{profileId}")
    ProfileCreationResponse getProfile(@PathVariable String profileId) {
        return userProfileService.getProfile(profileId);
    }
}