package com.gastonnicora.trips.exceptions.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gastonnicora.trips.exceptions.ErrorException;
import com.gastonnicora.trips.exceptions.dtos.ApiError;
import com.gastonnicora.trips.exceptions.dtos.ForbiddenApiError;
import com.gastonnicora.trips.exceptions.dtos.UnauthorizedApiError;
import com.gastonnicora.trips.exceptions.dtos.ValidationApiError;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Maneja globalmente las excepciones lanzadas por los controladores de la API.
 * 
 * Proporciona respuestas estandarizadas con DTOs de error, incluyendo:
 * - Errores de validación (400)
 * - Errores de autenticación (401)
 * - Acceso prohibido (403)
 * - Excepciones personalizadas de la aplicación
 *
 * Se documenta con Swagger/OpenAPI para que cada tipo de error tenga un ejemplo en la documentación.
 */
@RestControllerAdvice
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Errores de validación",
        content = @Content(schema = @Schema(implementation = ValidationApiError.class))
)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Error en autenticación",
        content = @Content(schema = @Schema(implementation = UnauthorizedApiError.class))
)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "Error de acceso",
        content = @Content(schema = @Schema(implementation = ForbiddenApiError.class))
)
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación lanzados por Spring cuando fallan las
     * anotaciones de validación (@NotBlank, @Size, etc.).
     * 
     * Devuelve un objeto ValidationApiError con un mapa de campos y mensajes de error.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationApiError handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = new HashMap<>();

        // Errores específicos de campo
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.computeIfAbsent(error.getField(), k -> new ArrayList<>())
                        .add(error.getDefaultMessage())
        );

        // Errores globales (por ejemplo @Valid en objetos anidados)
        ex.getBindingResult().getGlobalErrors().forEach(error ->
                errors.computeIfAbsent(error.getObjectName(), k -> new ArrayList<>())
                        .add(error.getDefaultMessage())
        );

        return new ValidationApiError(errors);
    }

    /**
     * Maneja las excepciones personalizadas de la aplicación.
     * 
     * Devuelve un ApiError con los detalles proporcionados en la excepción.
     */
    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ApiError> handleRuntime(ErrorException ex) {

        Map<String, List<String>> errors = ex.getErrors();

        ApiError apiError = new ApiError(
                ex.getStatus(),
                ex.getMessage(),
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(apiError, HttpStatus.valueOf(ex.getStatus()));
    }

    /**
     * Maneja excepciones de credenciales inválidas.
     * 
     * Devuelve UnauthorizedApiError con HTTP 401.
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public UnauthorizedApiError handleBadCredentialsException(BadCredentialsException ex) {
        return new UnauthorizedApiError("No autenticado o token inválido");
    }

    /**
     * Maneja excepciones de token JWT inválido o expirado.
     * 
     * Devuelve ApiError con HTTP 401.
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleBadTokenException(JwtException ex) {
        return new ApiError(401, "Token inválido o expirado");
    }
}