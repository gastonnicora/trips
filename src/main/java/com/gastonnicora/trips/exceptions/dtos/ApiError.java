package com.gastonnicora.trips.exceptions.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Schema(description = "Error genérico")
public class ApiError {

    @Schema(description = "Estatus de respuesta", example = "400")
    private int status;
    @Schema(description = "Mensaje de error", example = "Error en la validación")
    private String message;
    @Schema(description = "Cuando se genero el error")
    private LocalDateTime timestamp;
    @Schema(description = "Listado de errores", example = "{\"email\":[\"El email no puede quedar en blanco\"] }")
    private Map<String, List<String>> errors;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}