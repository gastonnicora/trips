package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {
    private final int status = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedException(String message) {
        super(message);
    }

    public int getStatus() {
        return status;
    }


}
