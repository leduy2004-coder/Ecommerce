package com.ecommerce.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // Kích hoạt Feign Clients
public class IdentityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityServiceApplication.class, args);
	}

//	@Bean
//	CommandLineRunner loadData(RoleRepository roleRepository) {
//		return args -> {
//			if (roleRepository.count() == 0) {
//				roleRepository.save(new RoleEntity("ADMIN", "admin"));
//				roleRepository.save(new RoleEntity("USER", "user"));
//			}
//		};
//	}

}
