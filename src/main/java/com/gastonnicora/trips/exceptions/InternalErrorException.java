package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada utilizada para representar errores internos del
 * servidor.
 * <p>
 * Corresponde al código de estado HTTP 500 (Internal Server Error) y se utiliza
 * cuando ocurre un error durante el procesamiento de una operación que impide
 * completar la solicitud correctamente.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-22
 */
public class InternalErrorException extends RuntimeException {

    /**
     * Código de estado HTTP asociado a la excepción.
     */
    private final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    /**
     * Explicación adicional asociada al error.
     */
    private final String explain;

    /**
     * Constructor que inicializa la excepción con un mensaje y una explicación.
     *
     * @param message Mensaje descriptivo del error.
     * @param explain Explicación adicional asociada al error.
     */
    public InternalErrorException(String message, String explain) {
        super(message);
        this.explain = explain;
    }

    /**
     * Obtiene la explicación adicional asociada al error.
     *
     * @return Explicación adicional del error.
     */
    public String getExplain() {
        return explain;
    }

    /**
     * Obtiene el código de estado HTTP asociado a la excepción.
     *
     * @return Código de estado HTTP 500.
     */
    public int getStatus() {
        return status;
    }

}