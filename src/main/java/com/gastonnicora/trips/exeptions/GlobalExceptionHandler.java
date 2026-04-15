package com.gastonnicora.trips.exeptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.JwtException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ex.getBindingResult().getGlobalErrors()
                .forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));

        return new ApiError(
                400,
                "Error en la validación",
                LocalDateTime.now(),
                errors);
    }

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<?> handleRuntime(ErrorException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ApiError(
                ex.getStatus(),
                ex.getMessage(),
                LocalDateTime.now(),
                ex.getErrors()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleBadCredentialsException(BadCredentialsException ex) {

        return new ApiError(
                401,
                "Email o contraseña incorrectos",
                LocalDateTime.now(),
                null);
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleBadTokenException(JwtException ex) {

        return new ApiError(
                403,
                ex.getMessage(),
                LocalDateTime.now(),
                null);
    }

}