package com.gastonnicora.trips.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.gastonnicora.trips.services.UserService;

@Configuration
public class DataInitializer {
    @Value("${super.email}")
    private String email;
    @Value("${super.password}")
    private String password;

    @Bean
    CommandLineRunner init(UserService userService) {
        return args -> {
            userService.createSuperAdminIfNotExists(email, password);
        };
    }
}
