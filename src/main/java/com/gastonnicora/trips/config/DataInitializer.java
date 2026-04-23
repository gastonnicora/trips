package com.gastonnicora.trips.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gastonnicora.trips.services.UserService;

@Configuration
public class DataInitializer {
    @Value("${superadmin.email}")
    private String email;
    @Value("${superadmin.password}")
    private String password;

    @Bean
    CommandLineRunner init(UserService userService) {
        return args -> {
            userService.createSuperAdminIfNotExists(email, password);
        };
    }
}
