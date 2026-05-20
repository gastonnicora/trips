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
import com.gastonnicora.trips.security.handlers.CustomAccessDeniedHandler;
import com.gastonnicora.trips.security.handlers.CustomAuthenticationEntryPoint;

/**
 * Configuración de seguridad de la aplicación.
 * <p>
 * Esta clase configura la seguridad de Spring Security para la plataforma,
 * incluyendo la autenticación JWT para la API y la autenticación de formularios
 * para la web. También define codificación de contraseñas y manejo de
 * excepciones personalizadas.
 * </p>
 * 
 * <ul>
 * <li>Define los filtros de seguridad para las rutas de la API.</li>
 * <li>Define los filtros de seguridad para la web.</li>
 * <li>Maneja accesos denegados (403) y no autenticados (401) con handlers
 * personalizados.</li>
 * </ul>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final UserDetailsServiceImpl userService;
        private final JwtAuthenticationFilter jwtFilter;
        private final CustomAccessDeniedHandler accessDeniedHandler;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;

        /**
         * Constructor de la clase SecurityConfig.
         * 
         * @param userService              ({@link UserDetailsServiceImpl}) Servicio de
         *                                 detalles de usuario.
         * @param jwtFilter                ({@link JwtAuthenticationFilter}) Filtro para
         *                                 autenticar peticiones JWT.
         * @param accessDeniedHandler      ({@link CustomAccessDeniedHandler}) Handler
         *                                 para accesos denegados (403).
         * @param authenticationEntryPoint ({@link CustomAuthenticationEntryPoint})Handler
         *                                 para no autenticados (401).
         */
        public SecurityConfig(UserDetailsServiceImpl userService,
                        JwtAuthenticationFilter jwtFilter,
                        CustomAccessDeniedHandler accessDeniedHandler,
                        CustomAuthenticationEntryPoint authenticationEntryPoint) {
                this.userService = userService;
                this.jwtFilter = jwtFilter;
                this.accessDeniedHandler = accessDeniedHandler;
                this.authenticationEntryPoint = authenticationEntryPoint;
        }

        /**
         * Bean que proporciona un codificador de contraseñas usando BCrypt.
         * <p>
         * Este codificador se utiliza para almacenar contraseñas seguras en la base de
         * datos.
         * </p>
         * 
         * @return PasswordEncoder instancia de BCryptPasswordEncoder
         */
        @Bean
        public PasswordEncoder codificaPass() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Bean que proporciona el AuthenticationManager.
         * <p>
         * Este bean permite la autenticación de usuarios a nivel global dentro de
         * Spring Security.
         * </p>
         * 
         * @param config ({@link AuthenticationConfiguration}) Configuración de
         *               autenticación de Spring Security.
         * @return {@link AuthenticationManager} instancia para manejar autenticación.
         * @throws Exception si ocurre algún error al obtener el AuthenticationManager.
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        /**
         * Configura la seguridad para la API con autenticación JWT.
         * <p>
         * - Desactiva CSRF.
         * - Aplica seguridad solo a rutas que empiezan con /api/**.
         * - Define excepciones personalizadas para 401 y 403.
         * - Permite accesos anónimos a /api/auth/login, /api/users (POST) y
         * /api/auth/refresh.
         * - Restringe el resto de la API a roles USER y ADMIN.
         * - Añade el filtro JWT antes del filtro de autenticación de Spring.
         * </p>
         * 
         * @param http ({@link HttpSecurity}) de Spring Security.
         * @return {@link SecurityFilterChain} cadena de filtros de seguridad
         *         configurada.
         * @throws Exception si ocurre un error al configurar la seguridad.
         * @see JwtAuthenticationFilter
         */
        @Bean
        @Order(1)
        public SecurityFilterChain jwtSecurityChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .securityMatcher("/api/**")
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/error", "/api/auth/refresh").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/users").anonymous()
                                                .requestMatchers("/api/auth/login").anonymous()
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * Configura la seguridad para la web (interfaz de usuario).
         * <p>
         * - Desactiva CSRF.
         * - Aplica seguridad a todas las rutas.
         * - Define excepciones personalizadas para 401 y 403.
         * - Permite accesos públicos a /auth/**, /v3/api-docs/**, /swagger-ui/** y
         * /public/**.
         * - Restringe el resto de la web a roles USER y ADMIN.
         * - Configura login por formulario y HTTP Basic.
         * - Configura logout invalidando la sesión y eliminando cookies.
         * </p>
         * 
         * @param http                  ({@link HttpSecurity}) de Spring Security.
         * @param authenticationManager ({@link AuthenticationManager}) para autenticar
         *                              usuarios.
         * @return {@link SecurityFilterChain} cadena de filtros de seguridad
         *         configurada.
         * @throws Exception si ocurre un error al configurar la seguridad.
         */
        @Bean
        @Order(2)
        public SecurityFilterChain securityChain(HttpSecurity http,
                        AuthenticationManager authenticationManager) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
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