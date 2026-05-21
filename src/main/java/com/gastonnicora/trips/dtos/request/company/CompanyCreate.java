package com.gastonnicora.trips.dtos.request.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una nueva empresa de transporte.
 * <p>
 * Contiene el nombre, email, teléfono y dirección de la empresa.
 * Aplica validaciones para que los campos no estén vacíos, no superen cierto
 * tamaño y
 * sean válidos.
 * </p>
 * <p>
 * Se utiliza típicamente en los endpoints de creación de empresas.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 * 
 */

@Schema(description = "DTO de empresa para creación (POST)")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyCreate {

    @Schema(description = "Nombre de la empresa", example = "Viajes LP")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @NotEmpty(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    @Schema(description = "Email de la empresa", example = "company@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @NotEmpty(message = "El email no puede quedar en blanco")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    @Email(message = "El email no es valido")
    private String email;

    @Schema(description = "Teléfono de la empresa", example = "+5491122334455")
    @NotBlank(message = "El teléfono no puede quedar en blanco")
    @NotEmpty(message = "El teléfono no puede quedar en blanco")
    @Size(max = 255, message = "El teléfono no puede tener mas de 255 caracteres")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "El teléfono es invalido. Ejemplos válidos: +5491122334455, 5491122334455, 91122334455")
    private String phone;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    @Schema(description = "Latitud de la dirección de la empresa", example = "-34.6037")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    @Schema(description = "Longitud de la dirección de la empresa", example = "-58.3816")
    private Double longitude;

}
