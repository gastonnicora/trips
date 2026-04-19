
package com.gastonnicora.trips.exeptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error de acceso prohibido")
public class ForbiddenApiError extends ApiError{

    @Schema(description = "Estatus de respuesta", example = "403")
    private int status;
    @Schema(description = "Mensaje de error", example = "Acceso denegado")
    private String message;
    @Schema(description = "Cuando se genero el error")
    private LocalDateTime timestamp;
    @Schema(description = "Listado de errores", example = "null")
    private Map<String, List<String>> errors;

  public ForbiddenApiError(String message) {
        super(
            403,
            message,
            java.time.LocalDateTime.now(),
            null
        );
    }
}
