package com.gastonnicora.trips.config;

import com.gastonnicora.trips.security.JwtAuthenticationFilter;
import com.gastonnicora.trips.security.UserDetailsServiceImpl;
import com.gastonnicora.trips.security.handlers.CustomAccessDeniedHandler;
import com.gastonnicora.trips.security.handlers.CustomAuthenticationEntryPoint;
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

/**
 * Configuración principal de seguridad de la aplicación.
 *
 * <p>
 * Define las cadenas de filtros de seguridad utilizadas para proteger la API
 * REST y la interfaz web. La API utiliza autenticación mediante JWT, mientras
 * que la interfaz web utiliza autenticación basada en sesión y HTTP Basic.
 * </p>
 *
 * <p>
 * También configura el codificador de contraseñas, el administrador de
 * autenticación y los handlers utilizados para gestionar errores de
 * autenticación y autorización.
 * </p>
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
     * Crea una instancia de la configuración de seguridad.
     *
     * @param userService              ({@link UserDetailsServiceImpl})servicio utilizado por Spring Security para
     *                                 cargar los
     *                                 datos de los usuarios
     * @param jwtFilter                ({@link JwtAuthenticationFilter})filtro encargado de procesar la autenticación
     *                                 mediante
     *                                 JWT
     * @param accessDeniedHandler      ({@link CustomAccessDeniedHandler}) handler encargado de gestionar accesos
     *                                 denegados
     * @param authenticationEntryPoint ({@link CustomAuthenticationEntryPoint})handler encargado de gestionar
     *                                 solicitudes no autenticadas
     */
    public SecurityConfig(
            UserDetailsServiceImpl userService,
            JwtAuthenticationFilter jwtFilter,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Proporciona el administrador de autenticación de Spring Security.
     *
     * @param config configuración utilizada por Spring Security para construir
     *               el administrador de autenticación
     *
     * @return instancia de {@link AuthenticationManager}
     *
     * @throws Exception si no es posible obtener el administrador de
     *                   autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Proporciona el codificador de contraseñas utilizado por la aplicación.
     *
     * <p>
     * Utiliza {@link BCryptPasswordEncoder} para aplicar un hash a las
     * contraseñas antes de almacenarlas.
     * </p>
     *
     * @return instancia de {@link PasswordEncoder} basada en BCrypt
     */
    @Bean
    public PasswordEncoder codificaPass() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad para la API REST.
     *
     * <p>
     * Esta cadena se aplica exclusivamente a las rutas que comienzan con
     * {@code /api/} y utiliza {@link JwtAuthenticationFilter} para procesar los
     * tokens JWT.
     * </p>
     *
     * <p>
     * Las rutas de autenticación y registro permitidas sin autenticación son
     * {@code /api/auth/login}, {@code POST /api/users} y
     * {@code /api/auth/refresh}. El resto de las solicitudes de la API requiere
     * un usuario autenticado.
     * </p>
     *
     * <p>
     * La autorización específica de cada operación se complementa mediante
     * seguridad a nivel de método.
     * </p>
     *
     * @param http objeto utilizado para configurar la seguridad HTTP
     *
     * @return cadena de filtros de seguridad configurada para la API
     *
     * @throws Exception si ocurre un error durante la configuración de
     *                   seguridad
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
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura la cadena de filtros de seguridad para la interfaz web.
     *
     * <p>
     * Permite el acceso público a las rutas de autenticación, documentación
     * OpenAPI y recursos públicos. El resto de las rutas requiere un usuario
     * con rol {@code ADMIN} o {@code USER}.
     * </p>
     *
     * <p>
     * La autenticación web utiliza inicio de sesión mediante formulario y HTTP
     * Basic. El cierre de sesión invalida la sesión HTTP y elimina la cookie
     * {@code JSESSIONID}.
     * </p>
     *
     * @param http                  objeto utilizado para configurar la seguridad
     *                              HTTP
     * @param authenticationManager administrador utilizado para autenticar
     *                              usuarios
     *
     * @return cadena de filtros de seguridad configurada para la interfaz web
     *
     * @throws Exception si ocurre un error durante la configuración de
     *                   seguridad
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityChain(
            HttpSecurity http,
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