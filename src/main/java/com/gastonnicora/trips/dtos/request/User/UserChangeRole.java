package com.gastonnicora.trips.dtos.request.user;

import java.util.HashSet;
import java.util.Set;

import com.gastonnicora.trips.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para solicitar la modificación de los roles globales de un
 * usuario.
 *
 * <p>
 * Contiene el conjunto de roles que serán asignados al usuario.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que se proporcione al menos un rol.
 * </p>
 *
 * <p>
 * Los roles disponibles están definidos en {@link Role}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Schema(description = "Datos necesarios para modificar los roles de un usuario")
@NoArgsConstructor
@Getter
public class UserChangeRole {

    /**
     * Conjunto de roles globales que serán asignados al usuario.
     *
     * <p>
     * El conjunto no puede ser {@code null} ni estar vacío.
     * </p>
     */
    @Schema(
            description = "Conjunto de roles globales que serán asignados al usuario.",
            example = "[\"USER\", \"ADMIN\"]"
    )
    @NotEmpty(message = "Debe seleccionar al menos un rol")
    @NotNull(message = "Debe seleccionar al menos un rol")
    private Set<Role> roles = new HashSet<>();

    /**
     * Crea una solicitud para modificar los roles de un usuario.
     *
     * <p>
     * Si el conjunto recibido es {@code null}, se inicializa un conjunto vacío.
     * </p>
     *
     * @param roles conjunto de {@link Role} que serán asignados al usuario
     */
    public UserChangeRole(Set<Role> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    /**
     * Establece el conjunto de roles del usuario.
     *
     * @param roles conjunto de roles que serán asignados al usuario
     */
    public void setRoles(Set<Role> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }
}
