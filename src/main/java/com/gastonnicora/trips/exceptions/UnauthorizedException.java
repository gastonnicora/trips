package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación para representar errores de
 * autenticación (HTTP 401 - Unauthorized).
 * <p>
 * Se utiliza cuando un cliente intenta acceder a un recurso sin estar
 * autenticado o cuando las credenciales proporcionadas no son válidas.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas con código HTTP 401.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-06
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (401 - Unauthorized).
     */
    private final int status = HttpStatus.UNAUTHORIZED.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 401.
     */
    public int getStatus() {
        return status;
    }
}
