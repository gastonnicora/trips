package com.gastonnicora.trips.dtos.entitys;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "DTO de usuario")
public class UserDTOs {

    private UUID uuid;
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    @Schema(description = "Apellido del usuario", example = "Perez")
    @NotBlank(message = "El apellido no puede quedar en blanco")
    @Size(max = 255, message = "El apellido no puede tener mas de 255 caracteres")
    private String lastname;

    @Schema(description = "Email del usuario", example = "juanperez@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    private String email;

    @Schema(description = "Roles del usuario", example = "[\"USER\"]")
    private Set<Role> role;

    @Schema(description = "Estado del usuario", example = "true")
    private boolean enabled;

    @Schema(description = "Fecha de creacion del usuario", example = "2023-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualizacion del usuario", example = "2023-01-01T00:00:00")
    private LocalDateTime updatedAt;

    public UserDTOs(UUID uuid, String name, String lastname, String email, Set<Role> role, boolean enabled,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.uuid = uuid;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
