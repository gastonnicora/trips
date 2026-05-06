package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar
 * solicitudes incorrectas o mal formadas (HTTP 400 - Bad Request).
 *
 * <p>
 * Se utiliza cuando el cliente envía una petición inválida desde el punto de
 * vista de la lógica de negocio o del formato de los datos, por ejemplo:
 * </p>
 * <ul>
 *   <li>Parámetros inválidos en la URL o request</li>
 *   <li>Datos inconsistentes en la solicitud</li>
 *   <li>Violaciones de reglas de negocio simples</li>
 * </ul>
 *
 * <p>
 * Esta excepción se diferencia de {@code ValidationException} en que no está
 * enfocada en errores de Bean Validation por campo, sino en errores generales
 * de solicitud.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas API estandarizadas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
public class BadRequestException extends RuntimeException {

    /** Código HTTP asociado al error (400 - Bad Request) */
    private final int status = HttpStatus.BAD_REQUEST.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 400
     */
    public int getStatus() {
        return status;
    }
}