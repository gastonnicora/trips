package com.gastonnicora.trips.dtos.request.User;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para actualizar los datos básicos de un usuario.
 * <p>
 * Hereda de {@link UserBasic} e incluye los campos:
 * </p>
 * <ul>
 *   <li>{@code name}: Nombre del usuario</li>
 *   <li>{@code lastname}: Apellido del usuario</li>
 *   <li>{@code email}: Correo electrónico del usuario</li>
 * </ul>
 * <p>
 * Se utiliza típicamente en los endpoints de actualización de usuarios (PUT).
 * </p>
 * 
 * <p>
 * Ejemplo de uso:
 * </p>
 * <pre>
 * UserPut userPut = new UserPut("Gastón", "Nicora", "gaston@example.com");
 * String nombre = userPut.getName();
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Schema(description = "DTO de usuario para actualización (PUT)")
public class UserPut extends UserBasic {

    /**
     * Constructor completo.
     * 
     * @param name Nombre del usuario
     * @param lastname Apellido del usuario
     * @param email Correo electrónico
     */
    public UserPut(String name, String lastname, String email) {
        super(name, lastname, email);
    }
}