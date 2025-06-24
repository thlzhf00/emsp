package com.example.emsp.interfaces.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalTime;

/**
 * DTO for BusinessHours. Used in request and response bodies.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class BusinessHoursDto {
    @NotNull(message = "Opening time cannot be null")
    LocalTime opensAt;
    @NotNull(message = "Closing time cannot be null")
    LocalTime closesAt;
}