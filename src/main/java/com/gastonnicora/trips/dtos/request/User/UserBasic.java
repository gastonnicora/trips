package com.gastonnicora.trips.dtos.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO básico de usuario.
 * <p>
 * Clase abstracta que contiene la información básica y común de un usuario.
 * </p>
 *
 * <p>
 * Se utiliza como clase base para los DTOs relacionados con la creación y
 * actualización de usuarios, evitando duplicar la definición de los campos
 * comunes.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que los datos recibidos cumplan con los requisitos establecidos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
@Schema(description = "DTO básico de usuario utilizado como clase base para Requests")
@NoArgsConstructor
@Getter
@Setter
public abstract class UserBasic {

    /**
     * Nombre del usuario.
     * <p>
     * El campo es obligatorio y no puede superar los 255 caracteres.
     * </p>
     */
    @Schema(
            description = "Nombre del usuario",
            example = "Juan"
    )
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    /**
     * Apellido del usuario.
     * <p>
     * El campo es obligatorio y no puede superar los 255 caracteres.
     * </p>
     */
    @Schema(
            description = "Apellido del usuario",
            example = "Perez"
    )
    @NotBlank(message = "El apellido no puede quedar en blanco")
    @Size(max = 255, message = "El apellido no puede tener mas de 255 caracteres")
    private String lastname;

    /**
     * Dirección de correo electrónico del usuario.
     * <p>
     * El campo es obligatorio, debe tener un formato de correo electrónico
     * válido y no puede superar los 255 caracteres.
     * </p>
     *
     * <p>
     * El valor se normaliza eliminando los espacios al inicio y al final y
     * convirtiéndolo a minúsculas al establecerlo mediante {@code setEmail}.
     * </p>
     */
    @Schema(
            description = "Dirección de correo electrónico del usuario",
            example = "juanperez@mail.com"
    )
    @NotBlank(message = "El email no puede quedar en blanco")
    @Email(message = "El email no es valido")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    private String email;

    /**
     * Constructor completo.
     *
     * @param name     Nombre del usuario.
     * @param lastname Apellido del usuario.
     * @param email    Email del usuario.
     */
    public UserBasic(String name, String lastname, String email) {
        this.name = name;
        this.lastname = lastname;
        this.email = email != null ? email.trim().toLowerCase() : null;
    }


    /**
     * Establece el email del usuario.
     * <p>
     * El valor recibido se normaliza eliminando los espacios al inicio y al
     * final y convirtiéndolo a minúsculas. Si el valor es {@code null}, se
     * mantiene como {@code null}.
     * </p>
     *
     * @param email Email del usuario.
     */
    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

}
