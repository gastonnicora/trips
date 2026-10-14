package com.gastonnicora.trips.dtos.request.user;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para solicitar la creación de un nuevo usuario.
 *
 * <p>
 * Hereda de {@link UserBasic} la información básica del usuario e incorpora la
 * contraseña y su confirmación.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que las contraseñas sean obligatorias y cumplan con la longitud
 * mínima y máxima establecida.
 * </p>
 *
 * <p>
 * La validación {@link FieldsMatch} garantiza que los campos {@code password} y
 * {@code confirmPassword} contengan el mismo valor.
 * </p>
 *
 * <p>
 * Se utiliza en los endpoints de registro de usuarios.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
@Schema(description = "Datos necesarios para crear un nuevo usuario")
@NoArgsConstructor
@Getter
@Setter
public class UserCreate extends UserBasic {

    /**
     * Longitud mínima requerida para la contraseña.
     */
    private final int minLengthPass = 8;

    /**
     * Contraseña del usuario.
     *
     * <p>
     * Es obligatoria y debe contener entre {@code minLengthPass} y 255
     * caracteres.
     * </p>
     */
    @Schema(
            description = "Contraseña del usuario con un mínimo de " + minLengthPass + " caracteres.",
            example = "12345678",
            format = "password",
            maxLength = 255
    )
    @NotBlank(message = "La contraseña no puede quedar en blanco")
    @Size(
            min = minLengthPass,
            max = 255,
            message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres"
    )
    private String password;

    /**
     * Confirmación de la contraseña del usuario.
     *
     * <p>
     * Es obligatoria y debe contener entre {@code minLengthPass} y 255
     * caracteres. Su valor debe coincidir con {@code password}.
     * </p>
     */
    @Schema(
            description = "Confirmación de la contraseña del usuario.",
            example = "12345678",
            format = "password",
            maxLength = 255
    )
    @NotBlank(message = "La contraseña no puede quedar en blanco")
    @Size(
            min = minLengthPass,
            max = 255,
            message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres"
    )
    private String confirmPassword;

    /**
     * Crea una solicitud para registrar un nuevo usuario.
     *
     * @param name nombre del usuario
     * @param lastname apellido del usuario
     * @param email dirección de correo electrónico del usuario
     * @param password contraseña del usuario
     * @param confirmPass confirmación de la contraseña
     */
    public UserCreate(
            String name,
            String lastname,
            String email,
            String password,
            String confirmPass) {

        super(name, lastname, email);
        this.password = password;
        this.confirmPassword = confirmPass;
    }

}
