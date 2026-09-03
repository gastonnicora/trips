package com.gastonnicora.trips.dtos.request.company;

import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.RoleCompany;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO de trabajador para creación (POST)")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkerCreate {

    @Schema(description = "UUID del usuario que se quiere agregar como trabajador", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotBlank(message = "El UUID del usuario no puede quedar en blanco")
    @NotEmpty(message = "El UUID del usuario no puede quedar vacío")
    private UUID userUuid;
    
    @Schema(description = "Roles del trabajador en la empresa", example = "[\"ADMIN\", \"DRIVER\"]")
    @NotEmpty(message = "El trabajador debe tener al menos un rol asignado")
    private Set<RoleCompany> roles;

}
