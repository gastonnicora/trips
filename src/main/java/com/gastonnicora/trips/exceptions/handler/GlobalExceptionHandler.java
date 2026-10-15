package com.gastonnicora.trips.exceptions.handler;

import com.gastonnicora.trips.exceptions.*;
import com.gastonnicora.trips.exceptions.dtos.*;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manejador global de excepciones de la API.
 * <p>
 * Centraliza el tratamiento de las excepciones producidas durante el
 * procesamiento de las solicitudes y genera respuestas estandarizadas mediante
 * los DTOs de error correspondientes.
 * </p>
 *
 * <p>
 * Contempla errores de validación, autenticación, autorización, conflictos,
 * recursos no encontrados, solicitudes incorrectas y errores internos del
 * servidor.
 * </p>
 *
 * <p>
 * Las respuestas generadas se documentan mediante Swagger/OpenAPI para
 * representar los diferentes tipos de error que puede devolver la API.
 * </p>
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
     * Maneja errores de autenticación producidos por credenciales inválidas.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 401.
     * </p>
     *
     * @param ex Excepción producida por credenciales inválidas.
     *
     * @return {@link UnauthorizedApiError} con el mensaje correspondiente al
     * error de autenticación.
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public UnauthorizedApiError handleBadCredentialsException(BadCredentialsException ex) {
        return new UnauthorizedApiError("No autenticado o token inválido");
    }

    /**
     * Maneja solicitudes incorrectas producidas por peticiones mal formadas o
     * argumentos no válidos.
     * <p>
     * Contempla, entre otros casos, errores al interpretar el contenido de la
     * solicitud y argumentos que no pueden procesarse correctamente.
     * </p>
     *
     * @param ex Excepción producida durante el procesamiento de la solicitud.
     *
     * @return {@link BadRequestApiError} con los detalles del error y código
     * HTTP 400.
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
     * Maneja la excepción personalizada de solicitud incorrecta.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 400 utilizando el
     * mensaje proporcionado por la excepción.
     * </p>
     *
     * @param ex {@link BadRequestException} producida durante el procesamiento
     *           de la solicitud.
     *
     * @return {@link BadRequestApiError} con los detalles de la excepción.
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestApiError handleBadRequest(BadRequestException ex) {
        return new BadRequestApiError(ex.getMessage());
    }

    /**
     * Maneja errores relacionados con tokens JWT inválidos o expirados.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 401.
     * </p>
     *
     * @param ex Excepción producida durante la validación del token JWT.
     *
     * @return {@link UnauthorizedApiError} indicando que el token no es válido
     * o ha expirado.
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public UnauthorizedApiError handleBadTokenException(JwtException ex) {
        return new UnauthorizedApiError("Token inválido o expirado");
    }

    /**
     * Maneja la excepción personalizada de conflicto.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 409 utilizando el
     * mensaje proporcionado por la excepción.
     * </p>
     *
     * @param ex {@link ConflictException} producida cuando existe un conflicto
     *           con el estado actual del recurso.
     *
     * @return {@link ConflictApiError} con los detalles de la excepción.
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ConflictApiError handleConflict(ConflictException ex) {
        return new ConflictApiError(ex.getMessage());
    }

    /**
     * Maneja las excepciones relacionadas con el acceso prohibido.
     * <p>
     * Contempla tanto la excepción personalizada {@link ForbiddenException}
     * como los errores de autorización generados por Spring Security mediante
     * {@link AuthorizationDeniedException}.
     * </p>
     *
     * @param ex Excepción producida cuando el acceso al recurso no está
     *           permitido.
     *
     * @return {@link ForbiddenApiError} con los detalles de la excepción y
     * código HTTP 403.
     */
    @ExceptionHandler({ForbiddenException.class, AuthorizationDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ForbiddenApiError handleForbidden(Exception ex) {
        return new ForbiddenApiError(ex.getMessage());
    }

    /**
     * Maneja errores internos producidos durante el procesamiento de una
     * solicitud y devuelve una respuesta estandarizada con código HTTP 500.
     * <p>
     * Captura excepciones de tipo {@link RuntimeException} y la excepción
     * personalizada {@link InternalErrorException}.
     * </p>
     *
     * <p>
     * Cuando la excepción corresponde a {@link InternalErrorException}, se
     * conserva su mensaje. Para el resto de las excepciones se devuelve un
     * mensaje genérico, evitando exponer detalles internos de la aplicación.
     * </p>
     *
     * <p>
     * La excepción capturada se registra en el sistema de logs con nivel
     * {@code ERROR}, incluyendo el stacktrace.
     * </p>
     *
     * @param ex Excepción capturada durante el procesamiento de la solicitud.
     *
     * @return {@link InternalServerErrorApiError} con la información del error
     * interno.
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
     * Maneja la excepción personalizada de recurso no encontrado.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 404 utilizando el
     * mensaje proporcionado por la excepción.
     * </p>
     *
     * @param ex {@link NotFoundException} producida cuando no se encuentra el
     *           recurso solicitado.
     *
     * @return {@link NotFoundApiError} con los detalles de la excepción.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NotFoundApiError handleNotFound(NotFoundException ex) {
        return new NotFoundApiError(ex.getMessage());
    }

    /**
     * Maneja la excepción personalizada de usuario no autenticado.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 401 utilizando el
     * mensaje proporcionado por la excepción.
     * </p>
     *
     * @param ex {@link UnauthorizedException} producida durante el proceso de
     *           autenticación.
     *
     * @return {@link UnauthorizedApiError} con los detalles de la excepción.
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public UnauthorizedApiError handleUnauthorized(UnauthorizedException ex) {
        return new UnauthorizedApiError(ex.getMessage());
    }

    /**
     * Maneja la excepción personalizada de validación.
     * <p>
     * Devuelve una respuesta estandarizada con código HTTP 400 utilizando los
     * errores proporcionados por la excepción.
     * </p>
     *
     * @param ex {@link ValidationException} que contiene los errores de
     *           validación.
     *
     * @return {@link ValidationApiError} con los errores de validación
     * proporcionados por la excepción.
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationApiError handleValidation(ValidationException ex) {
        return new ValidationApiError(ex.getErrors());
    }

    /**
     * Maneja los errores de validación producidos cuando los datos recibidos no
     * cumplen las restricciones de validación configuradas.
     * <p>
     * Recopila los errores específicos de cada campo y los errores globales en
     * un mapa, agrupando los mensajes asociados a cada elemento.
     * </p>
     *
     * @param ex Excepción de validación generada por Spring.
     *
     * @return {@link ValidationApiError} con los errores de validación
     * agrupados por campo u objeto.
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

}
