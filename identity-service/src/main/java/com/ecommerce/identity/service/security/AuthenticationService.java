package com.ecommerce.identity.service.security;

import com.ecommerce.identity.dto.request.AuthenticationRequest;
import com.ecommerce.identity.dto.request.IntrospectRequest;
import com.ecommerce.identity.dto.request.ProfileCreationRequest;
import com.ecommerce.identity.dto.request.UserRequest;
import com.ecommerce.identity.dto.response.AuthenticationResponse;
import com.ecommerce.identity.dto.response.IntrospectResponse;
import com.ecommerce.identity.entity.UserEntity;
import com.ecommerce.identity.exception.AppException;
import com.ecommerce.identity.exception.ErrorCode;
import com.ecommerce.identity.repository.UserRepository;
import com.ecommerce.identity.repository.feignClient.ProfileClient;
import com.ecommerce.identity.service.UserService;
import com.ecommerce.identity.service.impl.JwtService;
import com.ecommerce.identity.service.redis.TokenRedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    JwtService jwtService;
    UserService userService;
    UserRepository userRepository;
    TokenRedisService tokenRedisService;
    ProfileClient profileClient;
    ModelMapper modelMapper;
    KafkaTemplate<String, String> kafkaTemplate;

    @NonFinal
    @Value("${spring.application.security.jwt.secret-key}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request)  {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse register(UserRequest request) {
        UserEntity userSaver = userService.insert(request);

        var profileRequest = modelMapper.map(request, ProfileCreationRequest.class);
        profileRequest.setUserId(userSaver.getId());

        profileClient.createProfile(profileRequest);

        // Publish message to kafka
        kafkaTemplate.send("onboard-successful", "Welcome our new member " + userSaver.getUsername());

        var jwtToken = jwtService.generateToken(userSaver);
        var refreshToken = jwtService.generateRefreshToken(userSaver);
        tokenRedisService.saveRefreshToken(userSaver.getUsername(), refreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var jwtToken = "";
        var refreshToken = "";
        try {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            UserEntity user = userRepository.findByUsername(request.getUsername()).orElse(null);
            assert user != null;
            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

            jwtToken = jwtService.generateToken(user);
            refreshToken = jwtService.generateRefreshToken(user);
            tokenRedisService.saveRefreshToken(user.getUsername(), refreshToken);

        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        final String accessToken;
        final String userName;
        if (authHeader != null) {
            authHeader = authHeader.replaceAll("^\"|\"$", "");
            if (authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            } else
                throw new AppException(ErrorCode.TOKEN_INVALID);
        } else
            throw new AppException(ErrorCode.TOKEN_INVALID);
        userName = jwtService.extractUserName(accessToken);

        if (userName != null) {
            UserEntity user = userRepository.findByUsername(userName).orElse(null);

            String refreshToken = tokenRedisService.getRefreshToken(userName);
            if (refreshToken == null) throw new AppException(ErrorCode.RE_TOKEN_EXPIRED);
            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            assert user != null;
            tokenRedisService.saveRefreshToken(user.getUsername(), newRefreshToken);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        }
        return null;
    }

    private void verifyToken(String token) {
        try {
            // Parse và xác minh token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtService.getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Kiểm tra hạn token
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // Lấy username từ token
            String username = claims.getSubject();

            // Kiểm tra refresh token trong Redis
            if (tokenRedisService.getRefreshToken(username) == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

}
