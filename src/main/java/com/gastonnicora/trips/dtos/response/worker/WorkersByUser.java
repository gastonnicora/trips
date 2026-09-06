package com.gastonnicora.trips.dtos.response.worker;

import java.util.List;

import com.gastonnicora.trips.dtos.entities.UserDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) que representa la información de un usuario y sus
 * empresas donde trabaja.
 *
 * <p>
 * Se utiliza para exponer los datos de usuario y sus empresas donde trabaja en
 * respuestas de la API, sin incluir información sensible como la contraseña.
 * </p>
 *
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code user}: {@link UserDTO} del usuario.</li>
 * <li>{@code workers}: Lista de {@link WorkerCompany} de las empresas donde
 * trabaja.</li>
 * </ul>
 *
 * <p>
 * Ejemplo de JSON:
 * </p>
 *
 * <pre>
 * {
 *   "user": {
 *     "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *     "name": "Juan",
 *     "lastname": "Perez",
 *     "email": "juanperez@mail.com",
 *     "role": ["USER"],
 *     "enabled": true,
 *     "createdAt": "2026-01-01T00:00:00",
 *     "updatedAt": "2026-01-01T00:00:00"
 *   },
 *   "workers": [
 *     {
 *       "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *       "company": {
 *         "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *         "name": "Viajes LP",
 *         "address": "Calle Falsa 123,La Plata,Bs As, Argentina",
 *         "latitude": 500,
 *         "longitude": 500,
 *         "email": "company@mail.com",
 *         "phone": "+5491122334455",
 *         "createdAt": "2026-01-01T00:00:00",
 *         "updatedAt": "2026-01-01T00:00:00",
 *         "active": true
 *       },
 *       "roles": ["DRIVER"],
 *       "active": true
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de usuario y sus empresas donde trabaja")
public class WorkersByUser {

    /**
     * {@link UserDTO} del usuario.
     */
    @Schema(description = "DTO del usuario")
    private UserDTO user;

    /**
     * Lista de {@link WorkerCompany} de las empresas donde trabaja.
     */
    @Schema(description = "Lista de empresas donde trabaja")
    private List<WorkerCompany> workers;
}
