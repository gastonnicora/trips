package com.gastonnicora.trips.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gastonnicora.trips.security.JwtAuthenticationFilter;
import com.gastonnicora.trips.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final UserDetailsServiceImpl userService;
        private final JwtAuthenticationFilter jwtFilter;


        public SecurityConfig(UserDetailsServiceImpl userService, JwtAuthenticationFilter jwtFilter) {
                this.jwtFilter = jwtFilter;
                this.userService = userService;
        }

        @Bean
        public PasswordEncoder codificaPass() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        @Order(1)
        public SecurityFilterChain jwtSecurityChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .securityMatcher("/api/**")
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/error", "/api/auth/refresh").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/user").anonymous()
                                                .requestMatchers("/api/auth/login").anonymous()
                                                .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain securityChain(HttpSecurity http, AuthenticationManager authenticationManager)
                        throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .securityMatcher("/**")
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/error", "/auth/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html", "/public/**")
                                                .permitAll()
                                                .requestMatchers("/**").hasAnyRole("ADMIN", "USER")
                                                .anyRequest().authenticated())
                                .authenticationManager(authenticationManager)
                                .userDetailsService(userService)
                                .formLogin(form -> form.permitAll())
                                .httpBasic(basic -> {
                                })
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());

                return http.build();
        }

}
