package com.gastonnicora.trips.exeptions;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error de validación")
public class ValidationApiError extends ApiError {
    public ValidationApiError(Map<String, List<String>> errors) {
        super(
                400,
                "Error en la validación",
                java.time.LocalDateTime.now(),
                errors);
    }

}
