package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación utilizada para representar errores
 * de autenticación (HTTP 401 - Unauthorized).
 *
 * <p>
 * Se utiliza cuando un cliente intenta acceder a un recurso sin estar
 * autenticado o con credenciales inválidas, por ejemplo:
 * </p>
 * <ul>
 * <li>Token JWT inválido o expirado</li>
 * <li>Falta de cabecera de autorización</li>
 * <li>Usuario no autenticado intentando acceder a un endpoint protegido</li>
 * </ul>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler} para
 * generar respuestas API estandarizadas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Código HTTP asociado al error (401 - Unauthorized)
     */
    private final int status = HttpStatus.UNAUTHORIZED.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje de error
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 401
     */
    public int getStatus() {
        return status;
    }
}
