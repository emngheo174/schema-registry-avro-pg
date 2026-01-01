package com.example.sr.exception;

public class InvalidSchemaException extends RuntimeException {
    public InvalidSchemaException(String message) {
        super(message);
    }
}
