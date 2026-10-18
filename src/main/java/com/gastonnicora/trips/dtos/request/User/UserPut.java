package com.gastonnicora.trips.dtos.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para solicitar la actualización de los datos básicos de un
 * usuario.
 *
 * <p>
 * Hereda de {@link UserBasic} la información básica del usuario, incluyendo su
 * nombre, apellido y dirección de correo electrónico.
 * </p>
 *
 * <p>
 * Se utiliza en los endpoints de actualización de usuarios.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
@Schema(description = "Datos necesarios para actualizar la información básica de un usuario")
@NoArgsConstructor
public class UserPut extends UserBasic {

}
