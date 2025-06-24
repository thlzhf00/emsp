package com.example.emsp.interfaces.dtos;

import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.domain.evse.EVSEStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for responding with EVSE details.
 * Includes its unique EVSE ID (OCPI format), status, and list of Connectors.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 * Uses @Builder for convenient object creation.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
@Builder // Generates a builder for constructing instances
public class EVSEResponseDto {
    Long id;
    String evseId; // OCPI compliant EVSE ID string
    EVSEStatus status;
    Long locationId; // ID of the parent Location
    List<ConnectorResponseDto> connectors;
    LocalDateTime lastUpdated;

    /**
     * Converts a domain EVSE entity to an EVSEResponseDto.
     *
     * @param evse The EVSE entity.
     * @return A new EVSEResponseDto instance.
     */
    public static EVSEResponseDto fromEntity(EVSE evse) {
        return EVSEResponseDto.builder()
                .id(evse.getId())
                .evseId(evse.getEvseId().getText()) // Get the string value of EVSEId
                .status(evse.getStatus())
                .locationId(evse.getLocation().getId()) // Get parent Location's internal ID
                .connectors(evse.getConnectors().stream()
                        .map(ConnectorResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .lastUpdated(evse.getLastUpdated())
                .build();
    }
}