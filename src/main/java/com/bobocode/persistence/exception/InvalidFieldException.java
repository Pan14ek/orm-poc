package com.bobocode.persistence.exception;

public class InvalidFieldException extends RuntimeException {

    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
