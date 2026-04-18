package com.gastonnicora.trips.exeptions;

import java.util.HashMap;
import java.util.List;

public class ErrorException extends RuntimeException {
    private final int status;
    private HashMap<String, List<String>> errors = new HashMap<>();

    public ErrorException(String message, int status, HashMap<String, List<String>> errors) {
        super(message);
        this.status = status;
        this.errors = errors;

    }

    public ErrorException(String message, int status) {
        super(message);
        this.status = status;
    }

    public HashMap<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, List<String>> errors) {
        this.errors = errors;
    }

    public void addError(String field, String message) {
        this.errors.computeIfAbsent(field, k -> new java.util.ArrayList<>()).add(message);

    }

    public void removeError(String field) {
        errors.remove(field);
    }

    public int getStatus() {
        return status;
    }

}
