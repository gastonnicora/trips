package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación para representar conflictos con el
 * estado actual de un recurso (HTTP 409 - Conflict).
 * <p>
 * Se utiliza cuando una operación no puede completarse debido a un conflicto
 * con información existente o con el estado actual del recurso.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas con código HTTP 409.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-06
 */
public class ConflictException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (409 - Conflict).
     */
    private final int status = HttpStatus.CONFLICT.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 409.
     */
    public int getStatus() {
        return status;
    }
}
