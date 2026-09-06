package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar recursos
 * no encontrados (HTTP 404 - Not Found).
 *
 * <p>
 * Se utiliza cuando un cliente solicita un recurso que no existe o ha sido
 * eliminado, por ejemplo:
 * </p>
 * <ul>
 * <li>Vehiclecar un usuario por ID inexistente</li>
 * <li>Acceder a un recurso que ha sido borrado</li>
 * <li>Intentar obtener datos de una entidad que no se encuentra en la base de
 * datos</li>
 * </ul>
 *
 * <p>
 * Esta excepción se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler} para
 * generar respuestas API estandarizadas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
public class NotFoundException extends RuntimeException {

    /**
     * Código HTTP asociado al error (404 - Not Found)
     */
    private final int status = HttpStatus.NOT_FOUND.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 404
     */
    public int getStatus() {
        return status;
    }
}
