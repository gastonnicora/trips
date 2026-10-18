package com.gastonnicora.trips.dtos.request.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una nueva empresa de transporte.
 * <p>
 * Contiene los datos necesarios para registrar una empresa, incluyendo su
 * nombre, información de contacto y ubicación geográfica.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que los datos recibidos sean válidos antes de ser procesados.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-21
 *
 */
@Schema(description = "DTO de empresa para creación (POST)")
@NoArgsConstructor
@Getter
@Setter
public class CompanyCreate {

    /**
     * Nombre de la empresa.
     * <p>
     * El campo es obligatorio y no puede superar los 255 caracteres.
     * </p>
     */
    @Schema(description = "Nombre de la empresa", example = "Viajes LP")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    /**
     * Dirección de correo electrónico de la empresa.
     * <p>
     * El campo es obligatorio, debe tener un formato de correo electrónico
     * válido y no puede superar los 255 caracteres.
     * </p>
     *
     * <p>
     * El correo electrónico se normaliza a minúsculas y se eliminan los
     * espacios al inicio y al final cuando se establece mediante el método
     * {@code setEmail}.
     * </p>
     */
    @Schema(description = "Email de la empresa", example = "company@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    @Email(message = "El email no es valido")
    private String email;

    /**
     * Número de teléfono de la empresa.
     * <p>
     * El campo es obligatorio y debe cumplir con el formato establecido por la
     * expresión regular de validación.
     * </p>
     */
    @Schema(description = "Teléfono de la empresa", example = "+5491122334455")
    @NotBlank(message = "El teléfono no puede quedar en blanco")
    @Size(max = 255, message = "El teléfono no puede tener mas de 255 caracteres")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "El teléfono es invalido. Ejemplos válidos: +5491122334455, 5491122334455, 91122334455")
    private String phone;

    /**
     * Latitud geográfica de la ubicación de la empresa.
     * <p>
     * El campo es obligatorio y debe encontrarse dentro del rango válido de
     * latitudes, comprendido entre -90 y 90 grados.
     * </p>
     */
    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    @Schema(description = "Latitud de la dirección de la empresa", example = "-34.6037")
    private Double latitude;

    /**
     * Longitud geográfica de la ubicación de la empresa.
     * <p>
     * El campo es obligatorio y debe encontrarse dentro del rango válido de
     * longitudes, comprendido entre -180 y 180 grados.
     * </p>
     */
    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    @Schema(description = "Longitud de la dirección de la empresa", example = "-58.3816")
    private Double longitude;

    /**
     * Constructor completo.
     *
     * @param name      Nombre de la empresa
     * @param email     Email de la empresa
     * @param phone     Teléfono de la empresa
     * @param latitude  Latitud de la dirección de la empresa
     * @param longitude Longitud de la dirección de la empresa
     */
    public CompanyCreate(String name, String email, String phone, Double latitude, Double longitude) {
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Establece el email de la empresa.
     * <p>
     * El valor recibido se normaliza eliminando los espacios al inicio y al
     * final y convirtiéndolo a minúsculas. Si el valor es {@code null}, se
     * mantiene como {@code null}.
     * </p>
     *
     * @param email Email de la empresa
     */
    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

}
