package com.example.emsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception indicating that a requested resource was not found.
 * This exception maps to an HTTP 404 Not Found status.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Maps to HTTP 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}