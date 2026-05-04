package com.gastonnicora.trips.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Anotación de validación personalizada que asegura que dos campos de un objeto
 * tengan el mismo valor.
 * <p>
 * Útil, por ejemplo, para verificar que un campo "password" y un campo
 * "confirmPassword" sean idénticos al momento de crear o actualizar un usuario.
 * </p>
 *
 * <pre>
 * &#64;FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas no coinciden")
 * public class UserDto { ... }
 * </pre>
 *
 * <p>
 * Esta anotación se aplica a nivel de clase ({@link ElementType#TYPE}) y
 * es procesada por la clase {@link FieldsMatchValidator}.
 * </p>
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsMatchValidator.class)
@Documented
public @interface FieldsMatch {

    /**
     * Mensaje de error por defecto que se devuelve si los campos no coinciden.
     *
     * @return mensaje de error por defecto
     */
    String message() default "Fields do not match";

    /**
     * Nombre del primer campo a comparar.
     *
     * @return el nombre del campo principal
     */
    String field();

    /**
     * Nombre del segundo campo que debe coincidir con el primero.
     *
     * @return el nombre del campo que debe coincidir
     */
    String fieldMatch();

    /**
     * Grupos de validación a los que pertenece esta anotación.
     * <p>
     * Permite agrupar validaciones para ejecutarlas selectivamente.
     * </p>
     *
     * @return un arreglo de clases de grupos de validación
     */
    Class<?>[] groups() default {};

    /**
     * Payload que puede ser usado por los clientes para incluir información
     * adicional sobre la violación de la restricción.
     *
     * @return un arreglo de clases que extienden {@link Payload}
     */
    Class<? extends Payload>[] payload() default {};
}