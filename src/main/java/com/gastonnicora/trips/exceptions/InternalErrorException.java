package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar
 * errores internos del servidor (HTTP 500 - Internal Server Error).
 *
 * <p>
 * Se utiliza cuando ocurre un problema en el servidor y da un error no
 * esperado,
 * por ejemplo:
 * </p>
 * <ul>
 * <li>Error al conectar a la base de datos</li>
 * <li>Error en la codificación</li>
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
 * @since 2026-05-22
 */
public class InternalErrorException extends RuntimeException {
    /** Código HTTP asociado al error (500 - Internal Server Error) */
    private final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private String explain;

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     * @param explain Mensaje de explicación
     */
    public InternalErrorException(String message, String explain) {
        super(message);
        this.explain = explain;
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 500
     * 
     */
    public int getStatus() {
        return status;
    }

    public String getExplain() {
        return explain;
    }

}
