package com.gastonnicora.trips.dtos.request.user;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para solicitar el cambio de contraseña de un usuario.
 * <p>
 * Contiene la contraseña actual del usuario, la nueva contraseña y la
 * confirmación de la nueva contraseña.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que las contraseñas sean obligatorias y cumplan con la longitud
 * mínima y máxima establecida.
 * </p>
 *
 * <p>
 * La validación {@link FieldsMatch} garantiza que los campos {@code password}
 * y {@code confirmPassword} contengan el mismo valor.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
@Schema(description = "DTO utilizado para solicitar el cambio de contraseña de un usuario")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class UserChangePassword {

    private final int minLengthPass = 8;

    /**
     * Contraseña actual del usuario.
     * <p>
     * Es obligatoria y debe contener entre {@code minLengthPass} y 255
     * caracteres.
     * </p>
     */
    @Schema(
            description = "Contraseña actual del usuario",
            example = "12345678"
    )
    @NotBlank(message = "La contraseña actual no puede quedar en blanco")
    @Size(min = minLengthPass, max = 255, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres")
    private String passwordOld;

    /**
     * Nueva contraseña del usuario.
     * <p>
     * Es obligatoria y debe contener entre {@code minLengthPass} y 255
     * caracteres.
     * </p>
     */
    @Schema(
            description = "Nueva contraseña que reemplazará a la contraseña actual",
            example = "12345678"
    )
    @NotBlank(message = "La nueva contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, max = 255, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres")
    private String password;

    /**
     * Confirmación de la nueva contraseña.
     * <p>
     * Es obligatoria y debe contener entre {@code minLengthPass} y 255
     * caracteres. Su valor debe coincidir con {@code password}.
     * </p>
     */
    @Schema(
            description = "Confirmación de la nueva contraseña",
            example = "12345678"
    )
    @NotBlank(message = "La nueva contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, max = 255, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres")
    private String confirmPassword;

}
