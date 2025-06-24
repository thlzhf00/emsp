package com.example.emsp.interfaces.dtos;

import com.example.emsp.domain.evse.EVSEStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * DTO for creating a new EVSE.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class EVSECreateRequestDto {
    @NotBlank(message = "EVSE ID cannot be blank and must follow OCPI format (e.g., US*ABC*EVSE123)")
    String evseId; // This will be validated in the domain EVSEId value object constructor.

    // Although initial status is AVAILABLE, we might want to allow specifying it during creation
    // if the system design allows for different initial states later. For now, it's fixed to AVAILABLE.
    // If not allowing user to specify, remove this field from DTO.
    @NotNull(message = "EVSE status cannot be null")
    EVSEStatus status;
}