package com.bobocode.persistence.exception;

public class InvalidSQLExecutionException extends RuntimeException {

    public InvalidSQLExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
