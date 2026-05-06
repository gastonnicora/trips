package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar
 * acceso prohibido o denegado (HTTP 403 - Forbidden).
 *
 * <p>
 * Se utiliza cuando un usuario autenticado intenta acceder a un recurso
 * o realizar una acción para la cual no tiene permisos suficientes, por
 * ejemplo:
 * </p>
 * <ul>
 * <li>Acceder a un endpoint restringido sin rol adecuado</li>
 * <li>Modificar recursos de otros usuarios sin autorización</li>
 * <li>Violación de reglas de negocio que impiden la acción por falta de
 * permisos</li>
 * </ul>
 *
 * <p>
 * Esta excepción se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas API estandarizadas.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
public class ForbiddenException extends RuntimeException {

    /** Código HTTP asociado al error (403 - Forbidden) */
    private final int status = HttpStatus.FORBIDDEN.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     */
    public ForbiddenException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 403
     */
    public int getStatus() {
        return status;
    }
}