package com.gastonnicora.trips.controllers.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.request.auth.LoginRequest;
import com.gastonnicora.trips.dtos.request.auth.RefreshRequest;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.dtos.response.auth.RefreshResponse;
import com.gastonnicora.trips.entitys.RefreshToken;
import com.gastonnicora.trips.entitys.User;
import com.gastonnicora.trips.helpers.UserAgent;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.security.JwtService;
import com.gastonnicora.trips.services.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Endpoints para autenticación y autorización")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;


    @Value("${cookie.secure}")
    private boolean cookieSecure;

    public AuthController(AuthenticationManager authenticationManager,
            JwtService jwtService, RefreshTokenService refreshTokenService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión", description = "Inicia sesión con email y contraseña y recibe un token")
    public LoginResponse login(@Valid @RequestBody LoginRequest login, HttpServletRequest request,
            HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        User user= userRepository.findByEmailAndEnabledTrue(login.getEmail()).orElseThrow();
        String token = jwtService.generateToken(login.getEmail(),user.getVersion());

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String device = UserAgent.getDevice(userAgent);

        RefreshToken refreshTokenE = refreshTokenService.createToken(login.getEmail(), userAgent, ip,
                device);

        // WEB -> cookie
        if ("web".equals(device)) {
            addRefreshCookie(response, refreshTokenE.getToken());
        }

        //  ANDROID -> en body
        return 
                new LoginResponse(token,
                        "android".equals(device) ? refreshTokenE.getToken() : null);
    }

    // valida refreshToken, si es valido devuelve uno nuevo y un nuevo token de
    // acceso
    @PostMapping("/refresh")
    public RefreshResponse refresh(@CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body,
            HttpServletRequest request,
            HttpServletResponse response) {

        String token = null;
        // WEB
        if (cookieToken != null) {
            token = cookieToken;
        }
        // ANDROID
        if (body != null && body.getRefreshToken() != null) {
            token = body.getRefreshToken();
        }

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        RefreshToken rt = refreshTokenService.verifyToken(token, ip, userAgent);


        RefreshToken newRefresh = refreshTokenService.createToken(rt.getEmail(), userAgent, ip, rt.getDevice());
        refreshTokenService.revokeToken(token);
        User user= userRepository.findByEmailAndEnabledTrue(rt.getEmail()).orElseThrow();
        String newAccess = jwtService.generateToken(rt.getEmail(),user.getVersion());

        // WEB -> cookie
        if ("web".equals(rt.getDevice())) {
            addRefreshCookie(response, newRefresh.getToken());
        }
        return 
                new RefreshResponse(
                        newAccess,
                        "android".equals(rt.getDevice()) ? newRefresh.getToken() : null);

    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión actual.")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body,
            HttpServletResponse response) {

        String token = cookieToken != null
                ? cookieToken
                : (body != null ? body.getRefreshToken() : null);

        if (token != null) {
            refreshTokenService.revokeToken(token);
        }

        // limpiar cookie en web
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/api/auth/refresh");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();

    }

    private void addRefreshCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }
}
