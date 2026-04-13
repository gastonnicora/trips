package com.gastonnicora.trips.validations;

import java.lang.reflect.Field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String field;
    private String fieldMatch;
    private String message;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();

        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        try {

            Field firstField = value.getClass().getDeclaredField(field);
            Field secondField = value.getClass().getDeclaredField(fieldMatch);

            firstField.setAccessible(true);
            secondField.setAccessible(true);

            Object firstValue = firstField.get(value);
            Object secondValue = secondField.get(value);

            if (firstValue == null || secondValue == null) {
                return false;
            }

            boolean valid = firstValue.equals(secondValue);

            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(fieldMatch) 
                        .addConstraintViolation();
            }

            return valid;

        } catch (Exception e) {
            return false;
        }
    }
}
