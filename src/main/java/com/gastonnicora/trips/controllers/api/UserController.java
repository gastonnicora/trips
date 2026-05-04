package com.gastonnicora.trips.controllers.api;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.UserDTOs;
import com.gastonnicora.trips.dtos.request.User.UserChangePassword;
import com.gastonnicora.trips.dtos.request.User.UserChangeRole;
import com.gastonnicora.trips.dtos.request.User.UserCreate;
import com.gastonnicora.trips.dtos.request.User.UserPut;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.exceptions.ErrorException;
import com.gastonnicora.trips.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador para la gestión de usuarios.
 * <p>
 * Este controlador maneja todas las operaciones relacionadas con los usuarios,
 * como la creación, modificación, eliminación y obtención de usuarios a través
 * de los endpoints definidos en la URL "/api/users".
 * </p>
 * 
 * <p>
 * Este controlador utiliza el servicio {@link UserService} para realizar las
 * operaciones de negocio relacionadas con la gestión de usuarios.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Endpoints para la gestión de usuarios")
public class UserController {
    private final UserService userService;

    /**
     * Constructor del controlador UserController.
     * 
     * @param userService Servicio de usuario que maneja la lógica de negocio
     *                    relacionada con los usuarios.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene la lista de todos los usuarios.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * <strong>Importante:</strong> Este endpoint solo es accesible para usuarios
     * con roles "ADMIN" o "SUPER_ADMIN".
     * </p>
     * <p>
     * Los usuarios serán devueltos como una lista de objetos {@link UserDTOs}.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para obtener la
     * lista de usuarios.
     * </p>
     * 
     * @return ListResponse ({@link ListResponse}) de objetos UserDTO
     *         ({@link UserDTOs}) con todos los usuarios.
     * @see UserDTOs
     * @see UserService #getUsers()
     */
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Obtener usuarios", description = "Obtiene una lista de usuarios filtrados por un parámetro opcional")
    public ListResponse<UserDTOs> getUsers() {
        return userService.getUsers();
    }

    /**
     * Obtiene los datos del usuario actual.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * Los datos del usuario serán devueltos como un objeto {@link UserDTOs}.
     * </p>
     * <p>
     * Este endpoint utiliza el servicio {@link UserService} para obtener los datos
     * del usuario actual.
     * </p>
     * 
     * @return {@link UserDTOs} con los datos del usuario actual.
     * @see UserService #getCurrentUser()
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Usuario actual", description = "Devuelve los datos del usuario actual")
    public UserDTOs currentUser() {
        return userService.getCurrentUser();
    }

    /**
     * Obtiene un usuario por su UUID.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * <strong>Importante:</strong> Este endpoint solo es accesible para usuarios
     * con roles "ADMIN".
     * </p>
     * <p>
     * Este endpoint utiliza el servicio {@link UserService} para obtener los datos
     * del usuario con el UUID especificado.
     * </p>
     * 
     * @param uuid UUID del usuario a obtener.
     * @return {@link UserDTOs} con los datos del usuario con el UUID especificado.
     * @see UserService #getUserByUuid(UUID)
     */
    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su uuid")
    public UserDTOs getUserByUuid(@PathVariable UUID uuid) {
        return userService.getUserByUuid(uuid);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Este endpoint crea un nuevo usuario con los datos proporcionados. Se realiza
     * la validación de los datos antes de crear al usuario, y se verifica que el
     * correo electrónico no esté en uso.
     * </p>
     * <p>
     * En caso que los datos no sean válidos, Spring lanzará una excepción de tipo
     * {@link MethodArgumentNotValidException}.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para crear al
     * usuario en la base de datos.
     * </p>
     * 
     * @param userCreateRequest ({@link UserCreate}) con los datos válidos para el
     *               nuevo usuario.
     * @return {@link UserDTOs} con los datos del usuario recién creado.
     * @throws ErrorException Si el correo electrónico ya está en
     *                        uso por otro usuario.
     * @see UserService #createUser(UserCreate)
     */
    @PostMapping
    @Operation(summary = "Crear Usuario", description = "Crea un nuevo usuario")
    public UserDTOs createUser(@Valid @RequestBody UserCreate userCreateRequest) {
        return userService.createUser(userCreateRequest);
    }

    /**
     * Modifica los datos del usuario actual.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * Este endpoint modifica los datos del usuario actual con los datos
     * proporcionados. Se realiza la validación de los datos antes de crear al
     * usuario, y se verifica que el correo electrónico no esté en uso.
     * </p>
     * <p>
     * En caso que los datos no sean válidos, Spring lanzará una excepción de tipo
     * {@link MethodArgumentNotValidException}.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para actualizar los
     * datos del usuario.
     * </p>
     * 
     * @param userPutReques ({@link UserPut}) con los datos válidos para el
     *               nuevo usuario.
     * @return {@link UserDTOs} con los datos actualizados del usuario.
     * @throws ErrorException Si el correo electrónico ya está en uso por otro
     *                        usuario.
     * @see UserService #putCurrentUser(UserPut)
     */
    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar mi usuario", description = "Modifica mi usuario")
    public UserDTOs updateUserProfile(@Valid @RequestBody UserPut userPutReques) {
        return userService.putCurrentUser(userPutReques);
    }

    /**
     * Modifica la contraseña del usuario actual.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * Este endpoint modifica la contraseña del usuario actual con los datos
     * proporcionados. Se realiza la validación de los datos antes de modificar la
     * contraseña. Si se cambia correctamente la contraseña, se cierran todas las
     * sesiones.
     * </p>
     * <p>
     * En caso que los datos no sean válidos, Spring lanzará una excepción de tipo
     * {@link MethodArgumentNotValidException}.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para realizar el
     * cambio de contraseña.
     * </p>
     * 
     * @param userChangePasswordRequest ({@link UserChangePassword}) con los datos válidos para el
     *               cambio de contraseña.
     * @return {@link UserDTOs} con los datos del usuario.
     * @throws ErrorException Si la contraseña actual no es válida.
     * @see UserService #updatePassword(UserChangePassword)
     */
    @PutMapping("/me/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar mi contraseña", description = "Modifica mi contraseña")
    public UserDTOs changePassword(@Valid @RequestBody UserChangePassword userChangePasswordRequest) {
        return userService.updatePassword(userChangePasswordRequest);
    }

    /**
     * Modifica los roles de un usuario.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * <strong>Importante:</strong> Este endpoint solo es accesible para usuarios
     * con roles "ADMIN" o "SUPER_ADMIN".
     * </p>
     * <p>
     * Este endpoint modifica los roles de un usuario con los datos proporcionados.
     * Se realiza la validación de los datos antes de modificar los roles.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para modificar el
     * rol del usuario.
     * </p>
     * 
     * @param uuid   ({@link UUID}) del usuario a modificar.
     * @param userChangeRoleRequest ({@link UserChangeRole}) con los datos válidos para el
     *               cambio de roles.
     * @return {@link UserDTOs} con los datos del usuario.
     * @throws ErrorException Si el usuario no existe.
     * @throws ErrorException Si el rol no es válido.
     * @throws ErrorException Si se quiere cambiar el rol a un SUPER_ADMIN.
     * @see UserService #setRole(UUID, UserChangeRole)
     */
    @PutMapping("/{uuid}/role")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','HR_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar roles de un usuario", description = "Modifica un usuario por su uuid")
    public UserDTOs changeUserRole(@PathVariable UUID uuid, @Valid @RequestBody UserChangeRole userChangeRoleRequest) {
        return userService.setRole(uuid, userChangeRoleRequest);
    }

    /**
     * Elimina el usuario actual.
     * <p>
     * <strong>Requiere autenticación y autorización</strong>
     * </p>
     * <p>
     * Este endpoint elimina el usuario actual. Si se elimina correctamente se
     * cierran todas las sesiones.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para eliminar al
     * usuario actual.
     * </p>
     * 
     * @throws ErrorException Si el usuario no existe.
     * @see UserService #deleteCurrentUser()
     */
    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar mi usuario", description = "Elimina mi usuario")
    public void deleteCurrentUserAccount() {
        userService.deleteCurrentUser();
    }

}