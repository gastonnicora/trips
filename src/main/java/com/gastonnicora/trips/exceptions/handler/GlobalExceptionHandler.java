package com.gastonnicora.trips.exceptions.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.InternalErrorException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.exceptions.dtos.BadRequestApiError;
import com.gastonnicora.trips.exceptions.dtos.ConflictApiError;
import com.gastonnicora.trips.exceptions.dtos.ForbiddenApiError;
import com.gastonnicora.trips.exceptions.dtos.InternalServerErrorApiError;
import com.gastonnicora.trips.exceptions.dtos.NotFoundApiError;
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
 * - Conflicto de recursos (409)
 * - Recurso no encontrado (404)
 * - Solicitud incorrecta (400)
 * - Excepciones personalizadas de la aplicación
 *
 * Se documenta con Swagger/OpenAPI para que cada tipo de error tenga un ejemplo
 * en la documentación.
 */
@RestControllerAdvice
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de campos (Bean Validation)", content = @Content(schema = @Schema(implementation = ValidationApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud mal formada o inválida (JSON inválido, argumentos incorrectos)", content = @Content(schema = @Schema(implementation = BadRequestApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = NotFoundApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado o token inválido", content = @Content(schema = @Schema(implementation = UnauthorizedApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content(schema = @Schema(implementation = ForbiddenApiError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso (ej: email ya registrado)", content = @Content(schema = @Schema(implementation = ConflictApiError.class)))
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Maneja excepciones internas del servidor y devuelve una respuesta
         * estandarizada con código HTTP 500 (Internal Server Error).
         *
         * <p>
         * Este handler captura tanto excepciones genéricas de tipo
         * {@link RuntimeException} como excepciones personalizadas
         * {@link InternalErrorException}.
         * </p>
         *
         * <p>
         * En caso de recibir una {@link InternalErrorException},
         * se devuelve el mensaje específico de la excepción.
         * Para cualquier otra excepción no controlada,
         * se retorna un mensaje genérico para evitar exponer
         * detalles internos de la aplicación.
         * </p>
         *
         * <p>
         * Todas las excepciones son registradas en el sistema de logs
         * mediante nivel ERROR junto con el stacktrace completo,
         * permitiendo tareas de monitoreo y depuración.
         * </p>
         *
         * @param ex Excepción capturada durante el procesamiento de la solicitud
         * @return {@link InternalServerErrorApiError} con la información
         *         del error interno
         */
        @ExceptionHandler({
                        RuntimeException.class,
                        InternalErrorException.class
        })
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public InternalServerErrorApiError handleInternalErrors(Exception ex) {
                log.error("Error de interno del servidor", ex);
                if (ex instanceof InternalErrorException) {
                        return new InternalServerErrorApiError(ex.getMessage());
                } else {
                        return new InternalServerErrorApiError("Error interno del servidor");
                }

        }

        /**
         * Maneja errores de validación lanzados por Spring cuando fallan las
         * anotaciones de validación.
         * 
         * Devuelve un objeto ValidationApiError con un mapa de campos y mensajes de
         * error.
         * 
         * @param ex Excepción de validación de Spring
         * @return {@link ValidationApiError} con los errores de validación
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ValidationApiError handleValidationErrors(MethodArgumentNotValidException ex) {

                Map<String, List<String>> errors = new HashMap<>();

                // Errores específicos de campo
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.computeIfAbsent(error.getField(), k -> new ArrayList<>())
                                                .add(error.getDefaultMessage()));

                // Errores globales (por ejemplo @Valid en objetos anidados)
                ex.getBindingResult().getGlobalErrors()
                                .forEach(error -> errors.computeIfAbsent(error.getObjectName(), k -> new ArrayList<>())
                                                .add(error.getDefaultMessage()));

                return new ValidationApiError(errors);
        }

        /**
         * Maneja excepciones de credenciales inválidas.
         * 
         * Devuelve UnauthorizedApiError con HTTP 401.
         * 
         * @param ex Excepción de credenciales inválidas
         * @return {@link UnauthorizedApiError} con el mensaje de error
         */
        @ExceptionHandler(BadCredentialsException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public UnauthorizedApiError handleBadCredentialsException(BadCredentialsException ex) {
                return new UnauthorizedApiError("No autenticado o token inválido");
        }

        /**
         * Maneja excepciones de token JWT inválido o expirado.
         * 
         * Devuelve UnauthorizedApiError con HTTP 401.
         * 
         * @param ex Excepción de token JWT
         * @return {@link UnauthorizedApiError} con el mensaje de error
         */
        @ExceptionHandler(JwtException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public UnauthorizedApiError handleBadTokenException(JwtException ex) {
                return new UnauthorizedApiError("Token inválido o expirado");
        }

        /**
         * Maneja excepciones de solicitud incorrecta (HTTP 400).
         * <p>
         * Se ejecuta cuando el cliente envía una petición mal formada o inválida,
         * como JSON inválido o argumentos no válidos.
         * </p>
         *
         * Devuelve un {@link BadRequestApiError} con el mensaje de error y código HTTP
         * 400.
         *
         * @param ex excepción capturada (por ejemplo JSON mal formado o argumento
         *           inválido)
         * @return {@link BadRequestApiError} con los detalles del error
         */
        @ExceptionHandler({
                        HttpMessageNotReadableException.class,
                        IllegalArgumentException.class
        })
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public BadRequestApiError handleBadRequest(Exception ex) {
                return new BadRequestApiError("Solicitud inválida: " + ex.getMessage());
        }

        /**
         * Maneja la excepción personalizada de petición incorrecta.
         * 
         * Devuelve un {@link BadRequestApiError} con los detalles proporcionados en la
         * excepción y un código HTTP 400.
         * 
         * @param ex ({@link BadRequestException}) personalizada de la aplicación
         * @return {@link BadRequestApiError} con los detalles de la excepción
         */
        @ExceptionHandler(BadRequestException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public BadRequestApiError handleBadRequest(BadRequestException ex) {
                return new BadRequestApiError(ex.getMessage());
        }

        /**
         * Maneja la excepción personalizada de objeto no encontrado.
         * 
         * Devuelve un {@link NotFoundApiError} con los detalles proporcionados en la
         * excepción y un código HTTP 404.
         * 
         * @param ex ({@link NotFoundException}) personalizada de la aplicación
         * @return {@link NotFoundApiError} con los detalles de la excepción
         */
        @ExceptionHandler(NotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public NotFoundApiError handleNotFound(NotFoundException ex) {
                return new NotFoundApiError(ex.getMessage());
        }

        /**
         * Maneja la excepción personalizada de acceso prohibido.
         * 
         * Devuelve un {@link ForbiddenApiError} con los detalles proporcionados en la
         * excepción y un código HTTP 403.
         * 
         * @param ex ({@link ForbiddenException}) personalizada de la aplicación
         * @return {@link ForbiddenApiError} con los detalles de la excepción
         */
        @ExceptionHandler({ ForbiddenException.class, AuthorizationDeniedException.class })
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public ForbiddenApiError handleForbidden(Exception ex) {
                return new ForbiddenApiError(ex.getMessage());
        }

        /**
         * Maneja la excepción personalizada de no autenticado.
         * 
         * Devuelve un {@link UnauthorizedApiError} con los detalles proporcionados en
         * la excepción y un código HTTP 401.
         * 
         * @param ex ({@link UnauthorizedException}) personalizada de la aplicación
         * @return {@link UnauthorizedApiError} con los detalles de la excepción
         */
        @ExceptionHandler(UnauthorizedException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public UnauthorizedApiError handleUnauthorized(UnauthorizedException ex) {
                return new UnauthorizedApiError(ex.getMessage());
        }

        /**
         * Maneja la excepción personalizada de validación.
         * 
         * Devuelve un {@link ValidationApiError} con los detalles proporcionados en la
         * excepción y un código HTTP 400.
         * 
         * @param ex ({@link ValidationException}) personalizada de la aplicación
         * @return {@link ValidationApiError} con los detalles de la excepción
         */
        @ExceptionHandler(ValidationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ValidationApiError handleValidation(ValidationException ex) {
                return new ValidationApiError(ex.getErrors());
        }

        /**
         * Maneja la excepción personalizada de conflicto.
         * 
         * Devuelve un {@link ConflictApiError} con los detalles proporcionados en la
         * excepción y un código HTTP 409.
         * 
         * @param ex ({@link ConflictException}) personalizada de la aplicación
         * @return {@link ConflictApiError} con los detalles de la excepción
         */
        @ExceptionHandler(ConflictException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ConflictApiError handleConflict(ConflictException ex) {
                return new ConflictApiError(ex.getMessage());
        }

}