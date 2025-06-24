package com.example.emsp.interfaces.dtos;

import com.example.emsp.domain.evse.EVSEStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * DTO for updating the status of an EVSE.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class EVSEStatusUpdateRequestDto {
    @NotNull(message = "New status cannot be null")
    EVSEStatus newStatus;
}