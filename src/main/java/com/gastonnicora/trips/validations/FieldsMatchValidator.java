package com.gastonnicora.trips.validations;

import java.lang.reflect.Field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador personalizado que verifica que dos campos de un objeto sean
 * iguales.
 * Se usa junto con la anotación {@link FieldsMatch}.
 */
public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    // Nombre del primer campo que se quiere comparar
    private String field;

    // Nombre del segundo campo que se quiere comparar
    private String fieldMatch;

    // Mensaje de error a mostrar si la validación falla
    private String message;

    /**
     * Inicializa el validador con los valores definidos en la anotación
     * FieldsMatch.
     */
    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
        this.message = constraintAnnotation.message();
    }

    /**
     * Valida que los dos campos especificados tengan valores iguales.
     *
     * @param value   el objeto que se va a validar
     * @param context contexto de validación para agregar mensajes de error
     *                personalizados
     * @return {@code true} si los campos son iguales, {@code false} si no lo son o
     *         si ocurre algún error
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        try {
            // Obtiene los campos del objeto mediante reflexión
            Field firstField = value.getClass().getDeclaredField(field);
            Field secondField = value.getClass().getDeclaredField(fieldMatch);

            // Permite acceder a campos privados
            firstField.setAccessible(true);
            secondField.setAccessible(true);

            // Obtiene los valores actuales de los campos
            Object firstValue = firstField.get(value);
            Object secondValue = secondField.get(value);

            // Si alguno de los campos es null, la validación falla
            if (firstValue == null || secondValue == null) {
                return false;
            }

            // Compara los valores de los campos
            boolean valid = firstValue.equals(secondValue);

            // Si no coinciden, construye un mensaje de error personalizado
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(fieldMatch) // Se asigna el error al segundo campo
                        .addConstraintViolation();
            }

            // Retorna true si los valores coinciden, false si no
            return valid;

        } catch (Exception e) {
            // Si ocurre cualquier excepción (campo no encontrado, error de reflexión,
            // etc.), la validación falla
            return false;
        }
    }
}