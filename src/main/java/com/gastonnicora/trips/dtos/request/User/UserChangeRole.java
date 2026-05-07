package com.gastonnicora.trips.dtos.request.User;

import java.util.HashSet;
import java.util.Set;

import com.gastonnicora.trips.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

/**
 * DTO para cambiar los roles de un usuario.
 * <p>
 * Contiene un conjunto de roles que serán asignados al usuario.
 * Aplica validaciones para asegurar que al menos un rol sea seleccionado.
 * </p>
 * 
 * <p>
 * Se utiliza típicamente en endpoints de actualización de roles.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Schema(description = "DTO de usuario para cambiar roles")
@NoArgsConstructor
public class UserChangeRole {

    /**
     * Conjunto de roles asignados al usuario.
     */
    @Schema(description = "Conjunto de roles asignados al usuario", example = "[\"USER\", \"ADMIN\"]")
    @NotEmpty(message = "Debe seleccionar al menos un rol")
    @NotNull(message = "Debe seleccionar al menos un rol")
    private Set<Role> roles= new HashSet<>();

    /**
     * Constructor completo.
     * 
     * @param roles ({@link Set})Conjunto de {@link Role} asignados al usuario
     */
    public UserChangeRole(Set<Role> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}