package com.gastonnicora.trips.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar
 * errores de validación de múltiples campos en la solicitud (HTTP 400 - Bad
 * Request).
 *
 * <p>
 * Se utiliza cuando los datos enviados por el cliente no cumplen con las reglas
 * de negocio
 * o con la validación de los campos, por ejemplo:
 * </p>
 * <ul>
 * <li>Campos requeridos faltantes</li>
 * <li>Formato de datos inválido (email, fecha, números)</li>
 * <li>Reglas de negocio incumplidas</li>
 * </ul>
 *
 * <p>
 * Esta excepción permite enviar un mapa de errores donde la clave es el nombre
 * del campo
 * y el valor es una lista de mensajes de error asociados al mismo.
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
public class ValidationException extends RuntimeException {

    /** Código HTTP asociado al error (400 - Bad Request) */
    private final int status = HttpStatus.BAD_REQUEST.value();

    /** Mapa de errores por campo */
    private Map<String, List<String>> errors = null;

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo
     * y un mapa de errores por campo.
     *
     * @param message Mensaje de error
     * @param errors  Mapa de errores por campo
     */
    public ValidationException(String message, Map<String, List<String>> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 400
     */
    public int getStatus() {
        return status;
    }

    /**
     * Obtiene el mapa de errores por campo.
     *
     * @return Mapa de errores
     */
    public Map<String, List<String>> getErrors() {
        return errors;
    }

    /**
     * Establece un mapa de errores por campo.
     *
     * @param errors Mapa de errores
     */
    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }

    /**
     * Agrega un error para un campo específico.
     * Si el campo no existe en el mapa, se crea la lista automáticamente.
     *
     * @param field   Campo asociado al error
     * @param message Mensaje de error
     */
    public void addError(String field, String message) {
        if (errors == null) {
            errors = Map.of(field, List.of(message));
        } else {
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        }
    }
}