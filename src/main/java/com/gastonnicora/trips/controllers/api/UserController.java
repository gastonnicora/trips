package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.user.UserChangePassword;
import com.gastonnicora.trips.dtos.request.user.UserChangeRole;
import com.gastonnicora.trips.dtos.request.user.UserCreate;
import com.gastonnicora.trips.dtos.request.user.UserPut;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST encargado de la gestión de usuarios.
 *
 * <p>
 * Proporciona operaciones para consultar, crear, actualizar y eliminar
 * usuarios, así como para gestionar sus credenciales y roles globales.
 * </p>
 *
 * <p>
 * La lógica de negocio relacionada con los usuarios se delega en
 * {@link UserService}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Endpoints para la gestión de usuarios")
public class UserController {

    private final UserService userService;

    /**
     * Crea una instancia del controlador de usuarios.
     *
     * @param userService ({@link UserService}) servicio encargado de la gestión de usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * <p>
     * Al modificar correctamente la contraseña, se revocan las sesiones activas
     * del usuario.
     * </p>
     *
     * @param userChangePasswordRequest ({@link UserChangePassword}) datos necesarios para cambiar la contraseña
     *
     * @return datos del usuario actualizado
     *
     * @see UserService#updatePassword(UserChangePassword)
     */
    @PutMapping("/me/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado y revoca sus sesiones activas.")
    public UserDTO changePassword(@Valid @RequestBody UserChangePassword userChangePasswordRequest) {
        return userService.updatePassword(userChangePasswordRequest);
    }

    /**
     * Modifica los roles globales de un usuario.
     *
     * <p>
     * Esta operación requiere los roles {@code ADMIN}, {@code SUPER_ADMIN} o
     * {@code HR_MANAGER}.
     * </p>
     *
     * @param uuid                  UUID del usuario cuyos roles se modificarán
     * @param userChangeRoleRequest ({@link UserChangeRole}) nuevos roles globales del usuario
     *
     * @return datos actualizados del usuario
     *
     * @see UserService#setRole(UUID, UserChangeRole)
     */
    @PutMapping("/{uuid}/role")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','HR_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar roles de usuario", description = "Modifica los roles globales de un usuario.")
    public UserDTO changeUserRole(@PathVariable UUID uuid, @Valid @RequestBody UserChangeRole userChangeRoleRequest) {
        return userService.setRole(uuid, userChangeRoleRequest);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * <p>
     * Los datos recibidos se validan antes de delegar la creación al servicio
     * de usuarios.
     * </p>
     *
     * @param userCreateRequest ({@link UserCreate}) datos necesarios para crear el usuario
     *
     * @return datos del usuario creado
     *
     * @see UserService#createUser(UserCreate)
     */
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario a partir de los datos proporcionados.")
    public UserDTO createUser(@Valid @RequestBody UserCreate userCreateRequest) {
        return userService.createUser(userCreateRequest);
    }

    /**
     * Obtiene los datos del usuario autenticado.
     *
     * @return datos del usuario autenticado
     *
     * @see UserService#getCurrentUser()
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener usuario actual", description = "Devuelve los datos del usuario autenticado.")
    public UserDTO currentUser() {
        return userService.getCurrentUser();
    }

    /**
     * Elimina la cuenta del usuario autenticado.
     *
     * <p>
     * Al eliminar correctamente la cuenta, se revocan las sesiones activas
     * asociadas al usuario.
     * </p>
     *
     * @see UserService#deleteCurrentUser()
     */
    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar mi usuario", description = "Elimina la cuenta del usuario autenticado.")
    public void deleteCurrentUserAccount() {
        userService.deleteCurrentUser();
    }

    /**
     * Obtiene un usuario a partir de su UUID.
     *
     * <p>
     * Esta operación requiere los roles {@code ADMIN} o {@code SUPER_ADMIN}.
     * </p>
     *
     * @param uuid UUID del usuario que se desea consultar
     *
     * @return datos del usuario solicitado
     *
     * @see UserService#getUserByUuid(UUID)
     */
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener usuario", description = "Obtiene los datos de un usuario a partir de su UUID.")
    public UserDTO getUserByUuid(@PathVariable UUID uuid) {
        return userService.getUserByUuid(uuid);
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * <p>
     * Esta operación requiere los roles {@code ADMIN} o {@code SUPER_ADMIN}.
     * </p>
     *
     * @return respuesta con la lista de usuarios registrados
     *
     * @see UserService#getUsers()
     */
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Obtener usuarios", description = "Obtiene todos los usuarios registrados en el sistema.")
    public ListResponse<UserDTO> getUsers() {
        return userService.getUsers();
    }

    /**
     * Actualiza los datos del perfil del usuario autenticado.
     *
     * <p>
     * Los datos recibidos se validan antes de realizar la actualización.
     * </p>
     *
     * @param userPutRequest ({@link UserPut}) nuevos datos del perfil del usuario
     *
     * @return datos actualizados del usuario
     *
     * @see UserService#updateCurrentUser(UserPut)
     */
    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del perfil del usuario autenticado.")
    public UserDTO updateUserProfile(@Valid @RequestBody UserPut userPutRequest) {
        return userService.updateCurrentUser(userPutRequest);
    }

}