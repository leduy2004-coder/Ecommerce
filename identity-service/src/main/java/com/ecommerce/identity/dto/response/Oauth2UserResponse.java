package com.ecommerce.identity.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Oauth2UserResponse {

    // Information from Google
    GoogleUserInfo googleUserInfo;

    // Information from Facebook
    FacebookUserInfo facebookUserInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class GoogleUserInfo {
        String id; // Google user ID
        String name;
        String givenName;
        String familyName;
        String picture;
        String email;
        Boolean verifiedEmail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class FacebookUserInfo {

        String id;
        String name;
        FacebookPicture picture;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class FacebookPicture {
            FacebookPictureData data; // Picture data object
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class FacebookPictureData {
            String url;  // URL của ảnh
            Integer height;  // Chiều cao ảnh
            Integer width;  // Chiều rộng ảnh
            Boolean isSilhouette;  // Có phải là hình bóng không
        }
    }
}
