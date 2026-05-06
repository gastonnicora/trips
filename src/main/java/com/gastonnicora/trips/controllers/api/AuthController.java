package com.gastonnicora.trips.controllers.api;

import java.util.Optional;

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
import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.security.JwtService;
import com.gastonnicora.trips.services.RefreshTokenService;
import com.gastonnicora.trips.utils.UserAgent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controlador para autenticación y autorización de usuarios.
 * <p>
 * Proporciona endpoints para iniciar sesión, refrescar tokens y cerrar sesión.
 * Los tokens de acceso se generan mediante JWT y los tokens de refresco se
 * almacenan en cookies (para web) o en el cuerpo de respuesta (para mobile).
 * </p>
 * 
 * <p>
 * Maneja seguridad mediante Spring Security y JWT.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
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

    /**
     * Inicia sesión de un usuario.
     * <p>
     * Autentica al usuario mediante email y contraseña, genera un token JWT y un
     * refresh token.
     * Para dispositivos web, el refresh token se envía en una cookie segura. Para
     * dispositivos Android, se devuelve en el cuerpo de la respuesta.
     * </p>
     * 
     * @param login    ({@link LoginRequest}) Datos de inicio de sesión (email y
     *                 contraseña)
     * @param request  Petición HTTP para obtener información del usuario
     *                 (User-Agent, IP)
     * @param response Respuesta HTTP donde se puede agregar la cookie
     * @return {@link LoginResponse} con el token de acceso y opcionalmente el
     *         refresh token.
     * @throws UnauthorizedException Si las credenciales son inválidas.
     */
    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión", description = "Inicia sesión con email y contraseña y recibe un token")
    public LoginResponse login(@Valid @RequestBody LoginRequest login, HttpServletRequest request,
            HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        User user = userRepository.findByEmailAndEnabledTrue(login.getEmail()).orElseThrow();
        String token = jwtService.generateToken(login.getEmail(), user.getVersion(), user.getUuid());

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String device = UserAgent.getDevice(userAgent);

        RefreshToken refreshTokenE = refreshTokenService.createToken(token, user.getUuid(), userAgent, ip,
                device, user.getVersion());

        if ("web".equals(device)) {
            addRefreshCookie(response, refreshTokenE.getRefreshToken());
        }

        return new LoginResponse(token,
                "android".equals(device) ? refreshTokenE.getRefreshToken() : null);
    }

    /**
     * Refresca un token de acceso utilizando un refresh token válido.
     * <p>
     * Valida el refresh token (de cookie o body), genera un nuevo JWT y un nuevo
     * refresh token, y revoca el refresh token anterior.
     * Para web, el nuevo refresh token se envía en cookie. Para Android, en el
     * body.
     * </p>
     * 
     * @param cookieToken Refresh token enviado en cookie (opcional, para web)
     * @param body        ({@link RefreshRequest}) Refresh token enviado en body
     *                    (opcional, para mobile)
     * @param request     Petición HTTP
     * @param response    Respuesta HTTP para agregar la cookie (web)
     * @return {@link RefreshResponse} con el nuevo token de acceso y opcionalmente
     *         el refresh token
     * @throws UnauthorizedException Si el refresh token es inválido o expirado
     */
    @PostMapping("/refresh")
    public RefreshResponse refresh(@CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body,
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = null;

        if (cookieToken != null) {
            refreshToken = cookieToken;
        }
        if (body != null && body.getRefreshToken() != null) {
            refreshToken = body.getRefreshToken();
        }
        if (refreshToken == null) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        RefreshToken rt = refreshTokenService.verifyToken(refreshToken, ip, userAgent);

        User user = userRepository.findById(rt.getUserUuid()).orElseThrow();
        String newAccess = jwtService.generateToken(user.getEmail(), user.getVersion(), user.getUuid());

        RefreshToken newRefresh = refreshTokenService.createToken(newAccess, user.getUuid(), userAgent, ip,
                rt.getDevice(), user.getVersion());
        refreshTokenService.revokeToken(refreshToken);

        if ("web".equals(rt.getDevice())) {
            addRefreshCookie(response, newRefresh.getRefreshToken());
        }
        return new RefreshResponse(
                newAccess,
                "android".equals(rt.getDevice()) ? newRefresh.getToken() : null);
    }

    /**
     * Cierra la sesión del usuario.
     * <p>
     * Revoca el refresh token válido y elimina la cookie en caso de web.
     * </p>
     * 
     * @param cookieToken Refresh token enviado en cookie (opcional)
     * @param body        ({@link RefreshRequest}) Refresh token enviado en body
     *                    (opcional)
     * @param response    Respuesta HTTP para limpiar la cookie
     * @return {@link ResponseEntity} con estado 200 si la operación fue exitosa
     * @throws UnauthorizedException Si el refresh token es inválido o expirado
     */
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión actual.")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body,
            HttpServletResponse response) {

        String refreshToken = cookieToken != null
                ? cookieToken
                : (body != null ? body.getRefreshToken() : null);

        Optional<RefreshToken> rt = refreshTokenService.findByRefreshToken(refreshToken);
        if (refreshToken != null && !rt.isEmpty() && rt.get() != null) {
            refreshTokenService.revokeToken(refreshToken);
        } else {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        if ("web".equals(rt.get().getDevice())) {
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setMaxAge(0);
            cookie.setPath("/api/auth/refresh");
            cookie.setHttpOnly(true);
            cookie.setSecure(cookieSecure);
            response.addCookie(cookie);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Método auxiliar para agregar un refresh token en una cookie HTTP segura.
     * <p>
     * Se utiliza para usuarios web y establece el atributo HttpOnly y SameSite=Lax.
     * </p>
     * 
     * @param response     Respuesta HTTP donde se agrega la cookie
     * @param refreshToken Refresh token a almacenar en la cookie
     */
    private void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }
}