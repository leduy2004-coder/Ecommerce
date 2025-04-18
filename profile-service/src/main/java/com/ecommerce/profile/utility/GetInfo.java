package com.ecommerce.profile.utility;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetInfo {

    public static String getLoggedInUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem authentication có hợp lệ hay không
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // Hoặc ném exception nếu cần
        }
        // Trường hợp user đã đăng nhập và token là JWT
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getName();
        }
        return null;
    }
}
