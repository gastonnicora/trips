package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.request.auth.LoginRequest;
import com.gastonnicora.trips.dtos.request.auth.RefreshRequest;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.dtos.response.auth.RefreshResponse;
import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.security.JwtService;
import com.gastonnicora.trips.services.RefreshTokenService;
import com.gastonnicora.trips.utils.UserAgent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador REST encargado de la autenticación y gestión de sesiones de los
 * usuarios.
 *
 * <p>
 * Proporciona endpoints para iniciar sesión, renovar tokens de acceso y cerrar
 * sesión. Los tokens de acceso se generan mediante JWT y los refresh tokens se
 * gestionan según el dispositivo utilizado por el cliente.
 * </p>
 *
 * <p>
 * Para clientes web, el refresh token se almacena en una cookie HTTP. Para
 * clientes Android, el refresh token se devuelve en el cuerpo de la respuesta.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Endpoints para autenticación y gestión de sesiones")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    /**
     * Crea una instancia del controlador de autenticación.
     *
     * @param authenticationManager administrador utilizado para autenticar
     *                              usuarios
     * @param jwtService            ({@link JwtService}) servicio encargado de generar y gestionar tokens
     *                              JWT
     * @param refreshTokenService   ({@link RefreshTokenService}) servicio encargado de gestionar refresh tokens
     * @param userRepository        ({@link UserRepository}) repositorio utilizado para consultar usuarios
     */
    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService, RefreshTokenService refreshTokenService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    /**
     * Agrega el refresh token a una cookie HTTP para clientes web.
     *
     * <p>
     * La cookie se configura como {@code HttpOnly}, utiliza el atributo
     * {@code Secure} según la configuración de la aplicación y establece
     * {@code SameSite=Lax}.
     * </p>
     *
     * @param response     respuesta HTTP a la que se agrega la cookie
     * @param refreshToken refresh token que se almacenará en la cookie
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

    /**
     * Autentica a un usuario mediante su correo electrónico y contraseña.
     *
     * <p>
     * Tras una autenticación exitosa, genera un token de acceso JWT y un refresh
     * token asociado al usuario y al dispositivo desde el que se realiza la
     * solicitud.
     * </p>
     *
     * <p>
     * Para clientes web, el refresh token se almacena en una cookie. Para
     * clientes Android, se incluye en el cuerpo de la respuesta.
     * </p>
     *
     * @param login    datos de inicio de sesión del usuario ({@link LoginRequest}).
     * @param request  solicitud HTTP utilizada para obtener información del
     *                 cliente, como User-Agent e IP
     * @param response respuesta HTTP utilizada para establecer la cookie del
     *                 refresh token en clientes web
     *
     * @return {@link LoginResponse} con el token de acceso y, para clientes
     * Android, el refresh token
     *
     * @throws UnauthorizedException si las credenciales proporcionadas no son
     *                               válidas
     * @throws NotFoundException     si no se encuentra un usuario habilitado con el
     *                               correo electrónico proporcionado
     */
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario mediante email y contraseña y genera un token de acceso JWT. "
            + "Para clientes web, el refresh token se envía mediante una cookie; "
            + "para clientes Android, se devuelve en el cuerpo de la respuesta.")
    public LoginResponse login(@Valid @RequestBody LoginRequest login, HttpServletRequest request,
                               HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        User user = userRepository.findByEmailAndEnabledTrue(login.getEmail())
                .orElseThrow(() -> new NotFoundException("El usuario no fue encontrado"));
        String token = jwtService.generateToken(login.getEmail(), user.getVersion(), user.getUuid());

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String device = UserAgent.getDevice(userAgent);

        RefreshToken refreshTokenE = refreshTokenService.createToken(token, user, userAgent, ip,
                device, user.getVersion());

        if ("web".equals(device)) {
            addRefreshCookie(response, refreshTokenE.getRefreshToken());
        }

        return new LoginResponse(token,
                "android".equals(device) ? refreshTokenE.getRefreshToken() : null);
    }

    /**
     * Cierra la sesión asociada a un refresh token.
     *
     * <p>
     * Busca el refresh token proporcionado, lo revoca y, cuando corresponde a
     * un cliente web, elimina la cookie utilizada para almacenarlo.
     * </p>
     *
     * @param cookieToken refresh token recibido mediante cookie; puede ser
     *                    {@code null}
     * @param body        solicitud que contiene el refresh token para clientes
     *                    móviles; puede ser {@code null}
     * @param response    respuesta HTTP utilizada para eliminar la cookie del
     *                    refresh token
     *
     * @return {@link ResponseEntity} con estado HTTP 200 si la sesión se cerró
     * correctamente
     *
     * @throws UnauthorizedException si no se proporciona un refresh token
     *                               válido
     */
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token proporcionado y, para clientes web, elimina la cookie asociada.")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest body,
            HttpServletResponse response) {

        String refreshToken = cookieToken != null
                ? cookieToken
                : (body != null ? body.getRefreshToken() : null);

        Optional<RefreshToken> rt = refreshTokenService.findByRefreshToken(refreshToken);
        if (refreshToken != null && rt.isPresent()) {
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
     * Renueva un token de acceso utilizando un refresh token válido.
     *
     * <p>
     * El refresh token puede recibirse mediante una cookie para clientes web o
     * mediante el cuerpo de la solicitud para clientes móviles.
     * </p>
     *
     * <p>
     * Tras validar el refresh token, se genera un nuevo token de acceso y un
     * nuevo refresh token. El token anterior se revoca y el nuevo refresh token
     * se devuelve mediante cookie para clientes web o en el cuerpo de la
     * respuesta para clientes Android.
     * </p>
     *
     * @param cookieToken refresh token recibido mediante cookie; puede ser
     *                    {@code null}
     * @param body        solicitud que contiene el refresh token para clientes
     *                    móviles; puede ser {@code null}
     * @param request     solicitud HTTP utilizada para obtener información del
     *                    cliente, como User-Agent e IP
     * @param response    respuesta HTTP utilizada para establecer la nueva cookie
     *                    del refresh token
     *
     * @return {@link RefreshResponse} con el nuevo token de acceso y, para
     * clientes Android, el nuevo refresh token
     *
     * @throws UnauthorizedException si no se proporciona un refresh token
     *                               válido o si el token no es válido o ha expirado
     * @throws NotFoundException     si el usuario asociado al refresh token no
     *                               existe
     */
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado correctamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acceso", description = "Genera un nuevo token de acceso y un nuevo refresh token utilizando un refresh token válido. "
            + "El refresh token puede recibirse mediante cookie para clientes web "
            + "o mediante el cuerpo de la solicitud para clientes móviles.")
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

        User user = userRepository.findById(rt.getUser().getUuid())
                .orElseThrow(() -> new NotFoundException("El usuario no fue encontrado"));
        String newAccess = jwtService.generateToken(user.getEmail(), user.getVersion(), user.getUuid());

        RefreshToken newRefresh = refreshTokenService.createToken(newAccess, user, userAgent, ip,
                rt.getDevice(), user.getVersion());
        refreshTokenService.revokeToken(refreshToken);

        if ("web".equals(rt.getDevice())) {
            addRefreshCookie(response, newRefresh.getRefreshToken());
        }
        return new RefreshResponse(
                newAccess,
                "android".equals(rt.getDevice()) ? newRefresh.getToken() : null);
    }
}