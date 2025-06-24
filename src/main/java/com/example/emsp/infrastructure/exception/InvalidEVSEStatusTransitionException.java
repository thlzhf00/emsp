package com.example.emsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception indicating an invalid EVSE status transition.
 * This exception maps to an HTTP 409 Conflict status.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Maps to HTTP 409
public class InvalidEVSEStatusTransitionException extends RuntimeException {
    public InvalidEVSEStatusTransitionException(String message) {
        super(message);
    }
}