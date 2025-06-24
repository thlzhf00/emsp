package com.example.emsp.interfaces.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * DTO for requesting a new Location to be created.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class LocationRequestDto {
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