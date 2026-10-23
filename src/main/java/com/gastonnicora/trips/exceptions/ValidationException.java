package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excepción personalizada de la aplicación para representar errores de
 * validación de múltiples campos en la solicitud (HTTP 400 - Bad Request).
 *
 * <p>
 * Permite asociar uno o varios mensajes de error a los campos que presentan
 * errores de validación.
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
public class ValidationException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (400 - Bad Request).
     */
    private final int status = HttpStatus.BAD_REQUEST.value();

    /**
     * Mapa que contiene los errores asociados a los campos de la solicitud.
     *
     * <p>
     * La clave representa el nombre del campo y el valor contiene la lista de
     * mensajes de error asociados al mismo.
     * </p>
     */
    private Map<String, List<String>> errors = null;

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo y un
     * mapa de errores asociados a los campos de la solicitud.
     *
     * @param message Mensaje descriptivo del error.
     * @param errors  Mapa de errores asociado a los campos de la solicitud.
     */
    public ValidationException(String message, Map<String, List<String>> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Agrega un mensaje de error asociado a un campo específico.
     *
     * <p>
     * Si el mapa de errores no ha sido inicializado, se crea una estructura
     * inicial para almacenar el campo y su mensaje asociado.
     * </p>
     *
     * @param field   Nombre del campo que presenta el error de validación.
     * @param message Mensaje descriptivo del error asociado al campo.
     */
    public void addError(String field, String message) {
        if (errors == null) {
            errors = Map.of(field, List.of(message));
        } else {
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        }
    }

    /**
     * Obtiene el mapa de errores asociados a los campos de la solicitud.
     *
     * @return Mapa que relaciona cada campo con sus respectivos mensajes de error.
     */
    public Map<String, List<String>> getErrors() {
        return errors;
    }

    /**
     * Establece el mapa de errores asociados a los campos de la solicitud.
     *
     * @param errors Mapa que relaciona cada campo con sus respectivos mensajes de
     *               error.
     */
    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
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
