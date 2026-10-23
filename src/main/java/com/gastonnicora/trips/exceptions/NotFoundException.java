package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada utilizada para representar un recurso no encontrado.
 * <p>
 * Corresponde al código de estado HTTP 404 (Not Found) y se utiliza cuando un
 * cliente solicita un recurso que no existe o no se encuentra disponible.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-06
 */
public class NotFoundException extends RuntimeException {

    /**
     * Código de estado HTTP asociado a la excepción.
     */
    private final int status = HttpStatus.NOT_FOUND.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Obtiene el código de estado HTTP asociado a la excepción.
     *
     * @return Código de estado HTTP 404.
     */
    public int getStatus() {
        return status;
    }
}