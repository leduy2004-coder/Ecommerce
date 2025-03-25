package com.ecommerce.post.config;//package com.ecommerce.profile.config;
//
//
//import com.ecommerce.profile.utility.GetInfo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//
//import java.util.Objects;
//import java.util.Optional;
//
//@Configuration
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
//public class JpaAuditingConfig {
//
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return new AuditorAwareImpl();
//    }
//    @RequiredArgsConstructor
//    public static class AuditorAwareImpl implements AuditorAware<String> {
//
//        @Override
//        public Optional<String> getCurrentAuditor() {
//            try {
//                return Objects.requireNonNull(GetInfo.getLoggedInUserName()).describeConstable();
//            } catch (Exception e) {
//                return Optional.empty();
//            }
//        }
//    }
//}
