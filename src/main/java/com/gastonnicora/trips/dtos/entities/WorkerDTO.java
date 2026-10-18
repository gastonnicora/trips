package com.gastonnicora.trips.dtos.entities;

import com.gastonnicora.trips.enums.RoleCompany;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO utilizado para representar la relación entre un usuario y una empresa en
 * las respuestas de la API.
 *
 * <p>
 * Contiene la información del usuario asociado, la empresa, los roles asignados
 * dentro de la empresa y el estado de la relación.
 * </p>
 *
 * <p>
 * La clase se utiliza como objeto de transferencia de datos y no representa
 * directamente la entidad persistida en la base de datos.
 * </p>
 *
 * <p>
 * No incluye información sensible del usuario, como su contraseña.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-04
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de la relación entre un usuario y una empresa")
public class WorkerDTO {

    /**
     * Identificador único de la relación entre el usuario y la empresa.
     */
    @Schema(
            description = "Identificador único de la relación.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Información del usuario asociado a la empresa.
     */
    @Schema(
            description = "Información del usuario asociado a la empresa.",
            implementation = UserDTO.class
    )
    private UserDTO user;

    /**
     * Información de la empresa a la que pertenece el usuario.
     */
    @Schema(
            description = "Información de la empresa a la que pertenece el usuario.",
            implementation = CompanyDTO.class
    )
    private CompanyDTO company;

    /**
     * Conjunto de roles asignados al usuario dentro de la empresa.
     */
    @Schema(
            description = "Roles asignados al usuario dentro de la empresa.",
            example = "[\"DRIVER\"]"
    )
    private Set<RoleCompany> roles;

    /**
     * Indica si la relación entre el usuario y la empresa se encuentra activa.
     */
    @Schema(
            description = "Indica si la relación laboral se encuentra activa.",
            example = "true"
    )
    private boolean active;

    /**
     * Fecha y hora en la que se creó la relación entre el usuario y la empresa.
     */
    @Schema(
            description = "Fecha y hora de creación de la relación.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la relación entre el usuario y
     * la empresa.
     */
    @Schema(
            description = "Fecha y hora de la última actualización de la relación.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;
}
