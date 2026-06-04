package com.gastonnicora.trips.dtos.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) que representa la información de una empresa.
 * <p>
 * Se utiliza para exponer los datos de empresa en respuestas de la API, sin
 * incluir información sensible como la contraseña del dueño.
 * </p>
 * 
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único de la empresa.</li>
 * <li>{@code name}: Nombre de la empresa.</li>
 * <li>{@code address}: Dirección de la empresa.</li>
 * <li>{@code latitude}: Latitud de la dirección de la empresa.</li>
 * <li>{@code longitude}: Longitud de la dirección de la empresa.</li>
 * <li>{@code email}: Email de la empresa.</li>
 * <li>{@code phone}: Teléfono de la empresa.</li>
 * <li>{@code createdAt}: Fecha de creación de la empresa.</li>
 * <li>{@code updatedAt}: Fecha de última actualización de la empresa.</li>
 * <li>{@code active}: Estado de la empresa (activo o no).</li>
 * </ul>
 * 
 * <p>
 * Ejemplo de JSON:
 * </p>
 * 
 * <pre>
 * {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "Viajes LP",
 *   "address": "Calle Falsa 123,La Plata,Bs As, Argentina",
 *   "latitude": 500,
 *   "longitude": 500,
 *   "email": "company@mail.com",
 *   "phone": "+5491122334455",
 *   "createdAt": "2026-01-01T00:00:00",
 *   "updatedAt": "2026-01-01T00:00:00",
 *   "active": true
 * }
 * </pre>
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
@Schema(description = "DTO de empresa")
public class CompanyDTO {
    @Schema(description = "UUID de la empresa", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    @Schema(description = "Nombre de la empresa", example = "Viajes LP")
    private String name;

    @Schema(description = "Dirección de la empresa", example = "Calle Falsa 123,La Plata,Bs As, Argentina")
    private String address;

    @Schema(description = "Latitud de la dirección de la empresa", example = "500")
    private double latitude;

    @Schema(description = "Longitud de la dirección de la empresa", example = "500")
    private double longitude;

    @Schema(description = "Email de la empresa", example = "company@mail.com")
    private String email;

    @Schema(description = "Teléfono de la empresa", example = "+5491122334455")
    private String phone;

    @Schema(description = "Fecha de creación de la empresa", example = "2026-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualización de la empresa", example = "2026-01-01T00:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Estado de la empresa", example = "true")
    private boolean active;

}
