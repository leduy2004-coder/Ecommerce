package com.ecommerce.identity.service;

import com.ecommerce.identity.dto.request.UserRequest;
import com.ecommerce.identity.dto.response.AuthenticationResponse;
import com.ecommerce.identity.dto.response.ExchangeTokenResponse;
import com.ecommerce.identity.dto.response.Oauth2UserResponse;
import com.ecommerce.identity.entity.RoleEntity;
import com.ecommerce.identity.entity.UserEntity;
import com.ecommerce.identity.repository.UserRepository;
import com.ecommerce.identity.repository.feignClient.*;
import com.ecommerce.identity.service.impl.JwtService;
import com.ecommerce.identity.service.redis.TokenRedisService;
import com.ecommerce.identity.utility.enumUtils.AuthenticationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.example.ProfileCreationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2UserService {
    ModelMapper modelMapper;
    UserService userService;
    JwtService jwtService;
    TokenRedisService tokenRedisService;
    ProfileClient profileClient;
    AvatarClient avatarClient;
    UserRepository userRepository;
    GoogleIdentityClient googleIdentityClient;
    GoogleUserInfoClient googleUserInfoClient;
    FacebookUserInfoClient facebookUserInfoClient;
    FacebookIdentityClient facebookIdentityClient;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    protected String CLIENT_ID_FACEBOOK_CLIENT;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String CLIENT_ID_GOOGLE_CLIENT;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    protected String CLIENT_SECRET_FACEBOOK_CLIENT;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected String CLIENT_SECRET_GOOGLE_CLIENT;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    protected String REDIRECT_URI_FACEBOOK_CLIENT;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    protected String REDIRECT_URI_GOOGLE_CLIENT;
    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";
    @NonFinal
    String urlAvatar = null;

    @NonFinal
    String nickName = null;


    public AuthenticationResponse getUserInfo(String provider, String code) {
        Oauth2UserResponse oauth2UserResponse = new Oauth2UserResponse();
        String accessToken = outboundAuthenticate(provider, code);
        if ("google".equals(provider)) {
            Oauth2UserResponse.GoogleUserInfo googleUserInfo = googleUserInfoClient.getUserInfo(accessToken);
            oauth2UserResponse.setGoogleUserInfo(googleUserInfo);
        } else if ("facebook".equals(provider)) {
            Oauth2UserResponse.FacebookUserInfo facebookUserInfo = facebookUserInfoClient.getUserInfo(accessToken, "id,name,picture");
            oauth2UserResponse.setFacebookUserInfo(facebookUserInfo);
        } else {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
        return loadUser(oauth2UserResponse, provider);
    }

    public String outboundAuthenticate(String provider, String code) {
        String accessToken = null;

        if ("google".equals(provider)) {
            Map<String, String> request = new HashMap<>();
            request.put("code", code);
            request.put("client_id", CLIENT_ID_GOOGLE_CLIENT);
            request.put("client_secret", CLIENT_SECRET_GOOGLE_CLIENT);
            request.put("redirect_uri", REDIRECT_URI_GOOGLE_CLIENT);
            request.put("grant_type", GRANT_TYPE);
            ExchangeTokenResponse.ExchangeTokenGoogle response = googleIdentityClient.exchangeToken(request);
            accessToken = response.getAccessToken();
        } else if ("facebook".equals(provider)) {
            ExchangeTokenResponse.ExchangeTokenFaceBook response = facebookIdentityClient.exchangeToken(
                    code,
                    CLIENT_ID_FACEBOOK_CLIENT,
                    CLIENT_SECRET_FACEBOOK_CLIENT,
                    REDIRECT_URI_FACEBOOK_CLIENT,
                    GRANT_TYPE
            );
            accessToken = response.getAccessToken();
        }

        return accessToken;
    }

    public AuthenticationResponse loadUser(Oauth2UserResponse userRequest, String provider) {
        UserEntity userEntity = convertToUserEntity(userRequest, provider);

        UserEntity existingUser = userRepository.findByUsername(userEntity.getUsername()).orElse(null);
        if (existingUser != null) {
            tokenRedisService.clearByUserName(existingUser.getId());
        } else {
            existingUser = userService.insert(modelMapper.map(userEntity, UserRequest.class));
            profileClient.createProfile(ProfileCreationRequest.builder().nickName(nickName).build());
            avatarClient.createAvatar(urlAvatar, existingUser.getId());
        }
        return loginOauth2(existingUser);
    }

    public UserEntity convertToUserEntity(Oauth2UserResponse oauth2User, String clientName) {
        String provider = clientName.equalsIgnoreCase("google") ? AuthenticationType.GOOGLE.name() : AuthenticationType.FACEBOOK.name();
        List<RoleEntity> roles = new ArrayList<>();
        RoleEntity role = new RoleEntity();
        role.setCode("USER");
        roles.add(role);

        Object userInfo = null;

        if ("GOOGLE".equalsIgnoreCase(provider)) {
            userInfo = oauth2User.getGoogleUserInfo();
        } else {
            userInfo = oauth2User.getFacebookUserInfo();
        }

        String id = null;


        if (userInfo instanceof Oauth2UserResponse.GoogleUserInfo googleUserInfo) {
            id = googleUserInfo.getId();
            nickName = googleUserInfo.getName();
            urlAvatar = googleUserInfo.getPicture();
        } else {
            Oauth2UserResponse.FacebookUserInfo facebookUserInfo = (Oauth2UserResponse.FacebookUserInfo) userInfo;
            id = facebookUserInfo.getId();
            nickName = facebookUserInfo.getName();
            urlAvatar = facebookUserInfo.getPicture().getData().getUrl();
        }

        return UserEntity.builder()
                .authType(AuthenticationType.valueOf(provider))
                .status(true)
                .username(id)
                .roles(roles)
                .build();
    }

    public AuthenticationResponse loginOauth2(UserEntity user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenRedisService.saveRefreshToken(user.getId(), String.valueOf(refreshToken));

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}