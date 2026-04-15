package com.gastonnicora.trips.exeptions;

import java.util.HashMap;

public class ErrorException extends RuntimeException {
    private final int status;
    private HashMap<String, String> errors = new HashMap<>();



    public ErrorException(String message, int status,HashMap<String, String> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    
    }
    public ErrorException(String message, int status) {
        super(message);
        this.status = status;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }
    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }
    public void addError(String field, String message) {
        errors.put(field, message);
    }
    public void removeError(String field) {
        errors.remove(field);
    }

    public int getStatus() {
        return status;
    }

}
