package com.example.emsp.interfaces.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * DTO for Coordinates. Used in request and response bodies.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class CoordinatesDto {
    @NotNull(message = "Latitude cannot be null")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    Double latitude;
    @NotNull(message = "Longitude cannot be null")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    Double longitude;
}