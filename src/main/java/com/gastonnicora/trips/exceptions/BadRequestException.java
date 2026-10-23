package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación para representar solicitudes
 * incorrectas o inválidas (HTTP 400 - Bad Request).
 * <p>
 * Se utiliza cuando una solicitud no puede procesarse debido a información
 * inválida desde el punto de vista de la lógica de negocio o del formato de
 * los datos.
 * </p>
 *
 * <p>
 * Se diferencia de {@code ValidationException} en que representa errores
 * generales de la solicitud y no errores asociados específicamente a las
 * validaciones de Bean Validation.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas con código HTTP 400.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-06
 */
public class BadRequestException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (400 - Bad Request).
     */
    private final int status = HttpStatus.BAD_REQUEST.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 400.
     */
    public int getStatus() {
        return status;
    }
}
