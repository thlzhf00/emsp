package com.example.emsp.interfaces.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

/**
 * DTO for requesting a new Connector to be added.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class ConnectorRequestDto {
    @NotBlank(message = "Standard cannot be blank")
    String standard;
    @NotNull(message = "Power level cannot be null")
    @Positive(message = "Power level must be positive")
    Double powerLevel;
    @NotNull(message = "Voltage cannot be null")
    @Positive(message = "Voltage must be positive")
    Double voltage;
}