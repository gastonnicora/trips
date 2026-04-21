package com.gastonnicora.trips.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.dtos.request.User.UserChangePassword;
import com.gastonnicora.trips.dtos.request.User.UserChangeRole;
import com.gastonnicora.trips.dtos.request.User.UserCreate;
import com.gastonnicora.trips.dtos.request.User.UserPut;
import com.gastonnicora.trips.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User API", description = "Endpoints para la gestión de usuarios")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("s")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuarios", description = "Obtiene una lista de usuarios filtrados por un parámetro opcional")
    public List<UserDTOs> getUsers() {
        return new ArrayList<>();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Usuario actual", description = "Devuelve los datos del usuario actual")
    public UserDTOs currentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su uuid")
    public UserDTOs getUserByUuid(@PathVariable UUID uuid) {
        return userService.getUserByUuid(uuid);
    }

    @PostMapping
    @Operation(summary = "Crear Usuario", description = "Crea un nuevo usuario")
    public UserDTOs saveUser(@Valid @RequestBody UserCreate entity) {

        return userService.createUser(entity);
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar mi usuario", description = "Modifica mi usuario")
    public UserDTOs updateCurrentUser(@Valid @RequestBody UserPut entity) {
        return userService.putCurrentUser(entity);
    }

    @PutMapping("/changePassword")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar mi contraseña", description = "Modifica mi contraseña")
    public UserDTOs changePassword(@Valid @RequestBody UserChangePassword entity) {
        return userService.updatePassword(entity);

    }

    @PutMapping("/changeRole/{uuid}")
    @PreAuthorize("hasRole('ADMIN','SUPER_ADMIN','HR_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Modificar roles de un usuario", description = "Modifica un usuario por su uuid")
    public UserDTOs updateUserRole(@PathVariable UUID uuid, @Valid @RequestBody UserChangeRole entity) {
        return userService.setRole(uuid, entity);
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar mi usuario", description = "Elimina mi usuario")
    public void deleteCurrentUser() {
        userService.deleteCurrentUser();
    }

}
