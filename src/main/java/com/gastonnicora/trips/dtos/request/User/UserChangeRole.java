package com.gastonnicora.trips.dtos.request.User;

import java.util.Set;

import com.gastonnicora.trips.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO de usuario para put role")
public class UserChangeRole {
    @Schema(description = "Conjunto de roles asignados al usuario", example = "[\"USER\", \"ADMIN\"]")
    @NotEmpty(message = "Debe seleccionar al menos un rol")
    @NotNull(message = "Debe seleccionar al menos un rol")
    private Set<Role> roles;

    public UserChangeRole(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
