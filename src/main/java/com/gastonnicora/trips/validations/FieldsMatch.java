package com.gastonnicora.trips.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación de validación personalizada que verifica que dos campos de un
 * objeto tengan el mismo valor.
 *
 * <p>
 * Permite validar, por ejemplo, que los campos {@code password} y
 * {@code confirmPassword} sean idénticos al crear o actualizar un usuario.
 * </p>
 *
 * <pre>
 * &#64;FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas no coinciden")
 * public class UserDto { ... }
 * </pre>
 *
 * <p>
 * Esta anotación se aplica a nivel de clase mediante {@link ElementType#TYPE}
 * y es procesada por {@link FieldsMatchValidator}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsMatchValidator.class)
@Documented
public @interface FieldsMatch {

    /**
     * Define el nombre del primer campo que será comparado.
     *
     * @return nombre del primer campo a comparar.
     */
    String field();

    /**
     * Define el nombre del segundo campo que debe coincidir con el primero.
     *
     * @return nombre del segundo campo a comparar.
     */
    String fieldMatch();

    /**
     * Define los grupos de validación a los que pertenece esta anotación.
     *
     * <p>
     * Permite agrupar validaciones para ejecutarlas de forma selectiva.
     * </p>
     *
     * @return arreglo de clases correspondientes a los grupos de validación.
     */
    Class<?>[] groups() default {};

    /**
     * Define el mensaje de error por defecto cuando los campos no coinciden.
     *
     * @return mensaje de error por defecto.
     */
    String message() default "Fields do not match";

    /**
     * Define información adicional que puede ser asociada a la violación de
     * esta restricción.
     *
     * @return arreglo de clases que extienden {@link Payload}.
     */
    Class<? extends Payload>[] payload() default {};
}