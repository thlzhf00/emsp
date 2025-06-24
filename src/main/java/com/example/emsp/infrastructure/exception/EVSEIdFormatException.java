package com.example.emsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception indicating an invalid EVSE ID format.
 * This exception maps to an HTTP 400 Bad Request status.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Maps to HTTP 400
public class EVSEIdFormatException extends RuntimeException {
    public EVSEIdFormatException(String message) {
        super(message);
    }
}