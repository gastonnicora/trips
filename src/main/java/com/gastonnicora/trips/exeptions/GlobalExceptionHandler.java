package com.gastonnicora.trips.exeptions;

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

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Errores de validación", content = @Content(schema = @Schema(implementation = ValidationApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Error en autenticación", content = @Content(schema = @Schema(implementation = UnauthorizedApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Error de acceso", content = @Content(schema = @Schema(implementation = ForbiddenApiError.class)))
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ValidationApiError handleValidationErrors(MethodArgumentNotValidException ex) {

                Map<String, List<String>> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors
                                                .computeIfAbsent(error.getField(), k -> new ArrayList<>())
                                                .add(error.getDefaultMessage()));

                ex.getBindingResult().getGlobalErrors()
                                .forEach(error -> errors
                                                .computeIfAbsent(error.getObjectName(), k -> new ArrayList<>())
                                                .add(error.getDefaultMessage()));

                return new ValidationApiError(errors);
        }

        @ExceptionHandler(ErrorException.class)
        public ResponseEntity<ApiError> handleRuntime(ErrorException ex) {

                Map<String, List<String>> errors = ex.getErrors();

                ApiError apiError = new ApiError(
                                ex.getStatus(),
                                ex.getMessage(),
                                LocalDateTime.now(),
                                errors);

                return new ResponseEntity<>(apiError, HttpStatus.valueOf(ex.getStatus()));
        }

        @ExceptionHandler(BadCredentialsException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public UnauthorizedApiError handleBadCredentialsException(BadCredentialsException ex) {

                return new UnauthorizedApiError();
        }

        @ExceptionHandler(JwtException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public ApiError handleBadTokenException(JwtException ex) {

                return new ApiError(401, "Token inválido o expirado");

        }

}