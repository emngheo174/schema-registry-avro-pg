package com.example.sr.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSchemaException.class)
    public ResponseEntity<?> handleInvalidSchema(InvalidSchemaException e) {
        return ResponseEntity.badRequest().body(
            Map.of(
                "error_code", 40001,
                "message", e.getMessage()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e) {
        return ResponseEntity.status(500).body(
            Map.of(
                "error_code", 50000,
                "message", "Internal server error"
            )
        );
    }
}
