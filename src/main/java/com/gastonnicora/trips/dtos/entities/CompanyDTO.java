package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para representar una empresa en las respuestas de la API.
 *
 * <p>
 * Contiene únicamente la información necesaria para exponer los datos de una
 * empresa a los clientes de la aplicación.
 * </p>
 *
 * <p>
 * La clase se utiliza como objeto de transferencia de datos y no representa
 * directamente la entidad persistida en la base de datos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una empresa")
public class CompanyDTO {

    /**
     * Identificador único de la empresa.
     */
    @Schema(
            description = "Identificador único de la empresa.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Nombre comercial de la empresa.
     */
    @Schema(
            description = "Nombre de la empresa.",
            example = "Viajes LP"
    )
    private String name;

    /**
     * Dirección física de la empresa.
     */
    @Schema(
            description = "Dirección de la empresa.",
            example = "Calle Falsa 123, La Plata, Buenos Aires, Argentina"
    )
    private String address;

    /**
     * Latitud correspondiente a la ubicación de la empresa.
     */
    @Schema(
            description = "Latitud geográfica de la empresa.",
            example = "-34.9214"
    )
    private double latitude;

    /**
     * Longitud correspondiente a la ubicación de la empresa.
     */
    @Schema(
            description = "Longitud geográfica de la empresa.",
            example = "-57.9545"
    )
    private double longitude;

    /**
     * Dirección de correo electrónico utilizada como contacto de la empresa.
     */
    @Schema(
            description = "Correo electrónico de contacto de la empresa.",
            example = "company@mail.com"
    )
    private String email;

    /**
     * Número de teléfono utilizado como contacto de la empresa.
     */
    @Schema(
            description = "Número de teléfono de contacto de la empresa.",
            example = "+5491122334455"
    )
    private String phone;

    /**
     * Fecha y hora en la que se creó la empresa.
     */
    @Schema(
            description = "Fecha y hora de creación de la empresa.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la empresa.
     */
    @Schema(
            description = "Fecha y hora de la última actualización de la empresa.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si la empresa se encuentra activa.
     */
    @Schema(
            description = "Indica si la empresa se encuentra activa.",
            example = "true"
    )
    private boolean active;
}
