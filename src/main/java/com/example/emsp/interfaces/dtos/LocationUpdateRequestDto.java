package com.example.emsp.interfaces.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.lang.Nullable;

/**
 * DTO for updating an existing Location.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 * All fields are nullable here because they represent partial updates.
 * However, the application service will replace all fields.
 * If true partial updates are needed, consider using `Optional` or similar.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class LocationUpdateRequestDto {
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotBlank(message = "Address cannot be blank")
    String address;
    @Valid // Validates the nested CoordinatesDto
    @NotNull(message = "Coordinates cannot be null")
    CoordinatesDto coordinates;
    @Valid // Validates the nested BusinessHoursDto
    @NotNull(message = "Business hours cannot be null")
    BusinessHoursDto businessHours;
}