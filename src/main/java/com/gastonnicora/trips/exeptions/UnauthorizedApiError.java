package com.gastonnicora.trips.exeptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error de autenticación")
public class UnauthorizedApiError extends ApiError {

    
    @Schema(description = "Estatus de respuesta", example = "401")
    private int status;
    @Schema(description = "Mensaje de error", example = "Email o contraseña incorrectos")
    private String message;
    @Schema(description = "Cuando se genero el error")
    private LocalDateTime timestamp;
    @Schema(description = "Listado de errores", example = "null")
    private Map<String, List<String>> errors;
    
    public UnauthorizedApiError() {
        super(
            401,
            "Email o contraseña incorrectos",
            java.time.LocalDateTime.now(),
            null
        );
    }
}
