package com.gastonnicora.trips.exceptions;

import java.util.HashMap;
import java.util.List;

/**
 * Excepción personalizada de la aplicación que permite manejar errores
 * de negocio o validación de manera estructurada.
 * 
 * <p>
 * Contiene:
 * </p>
 * <ul>
 *   <li>{@code status}: Código HTTP asociado al error.</li>
 *   <li>{@code errors}: Mapa de campos y mensajes de error adicionales.</li>
 * </ul>
 * 
 * <p>
 * Se puede usar en combinación con {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas API estandarizadas.
 * </p>
 */
public class ErrorException extends RuntimeException {

    /** Código HTTP asociado al error */
    private final int status;

    /** Mapa de errores por campo */
    private HashMap<String, List<String>> errors = new HashMap<>();

    /**
     * Constructor completo con mensaje, estado y errores.
     * 
     * @param message Mensaje de error
     * @param status Código HTTP
     * @param errors Mapa de errores por campo
     */
    public ErrorException(String message, int status, HashMap<String, List<String>> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }

    /**
     * Constructor solo con mensaje y estado.
     * 
     * @param message Mensaje de error
     * @param status Código HTTP
     */
    public ErrorException(String message, int status) {
        super(message);
        this.status = status;
    }

    /**
     * Obtiene el mapa de errores por campo.
     * 
     * @return Mapa de errores
     */
    public HashMap<String, List<String>> getErrors() {
        return errors;
    }

    /**
     * Establece el mapa de errores.
     * 
     * @param errors Mapa de errores
     */
    public void setErrors(HashMap<String, List<String>> errors) {
        this.errors = errors;
    }

    /**
     * Agrega un error para un campo específico.
     * Si el campo no existe en el mapa, se crea la lista automáticamente.
     * 
     * @param field Campo asociado al error
     * @param message Mensaje de error
     */
    public void addError(String field, String message) {
        this.errors.computeIfAbsent(field, k -> new java.util.ArrayList<>()).add(message);
    }

    /**
     * Elimina todos los errores asociados a un campo.
     * 
     * @param field Campo a eliminar del mapa de errores
     */
    public void removeError(String field) {
        errors.remove(field);
    }

    /**
     * Obtiene el código de estado HTTP asociado a la excepción.
     * 
     * @return Código HTTP
     */
    public int getStatus() {
        return status;
    }

}