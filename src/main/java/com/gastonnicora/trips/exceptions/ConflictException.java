package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar
 * conflictos con el estado actual del recurso (HTTP 409 - Conflict).
 *
 * <p>
 * Se utiliza cuando una operación no puede completarse debido a un conflicto
 * con los datos existentes, por ejemplo:
 * </p>
 * <ul>
 *   <li>Intentar registrar un usuario con un email que ya existe</li>
 *   <li>Actualizar un recurso que ha sido modificado por otra operación</li>
 *   <li>Violaciones de reglas de negocio que impiden la acción solicitada</li>
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
public class ConflictException extends RuntimeException {

    /** Código HTTP asociado al error (409 - Conflict) */
    private final int status = HttpStatus.CONFLICT.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 409
     */
    public int getStatus() {
        return status;
    }
}