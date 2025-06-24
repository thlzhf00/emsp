package com.example.emsp.interfaces.dtos;

import com.example.emsp.domain.location.Connector;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for responding with Connector details.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 * Uses @Builder for convenient object creation.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
@Builder // Generates a builder for constructing instances
public class ConnectorResponseDto {
    Long id;
    String standard;
    Double powerLevel;
    Double voltage;
    Long evseId; // ID of the parent EVSE
    LocalDateTime lastUpdated;

    /**
     * Converts a domain Connector entity to a ConnectorResponseDto.
     *
     * @param connector The Connector entity.
     * @return A new ConnectorResponseDto instance.
     */
    public static ConnectorResponseDto fromEntity(Connector connector) {
        return ConnectorResponseDto.builder()
                .id(connector.getId())
                .standard(connector.getStandard())
                .powerLevel(connector.getPowerLevel())
                .voltage(connector.getVoltage())
                .evseId(connector.getEvse().getId()) // Get parent EVSE's internal ID
                .lastUpdated(connector.getLastUpdated())
                .build();
    }
}