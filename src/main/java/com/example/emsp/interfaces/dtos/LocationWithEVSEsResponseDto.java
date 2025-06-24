package com.example.emsp.interfaces.dtos;

import com.example.emsp.domain.location.Location;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for responding with Location details including its EVSEs.
 * Used for the query by last_updated endpoint.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 * Uses @Builder for convenient object creation.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
@Builder // Generates a builder for constructing instances
public class LocationWithEVSEsResponseDto {
    Long id;
    String name;
    String address;
    CoordinatesDto coordinates;
    BusinessHoursDto businessHours;
    List<EVSEResponseDto> evses; // List of EVSEs associated with this location
    LocalDateTime lastUpdated;

    /**
     * Converts a domain Location entity to a LocationWithEVSEsResponseDto.
     * This method also converts all associated EVSE entities.
     *
     * @param location The Location entity.
     * @return A new LocationWithEVSEsResponseDto instance.
     */
    public static LocationWithEVSEsResponseDto fromEntity(Location location) {
        return LocationWithEVSEsResponseDto.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .coordinates(new CoordinatesDto(location.getCoordinates().getLatitude(), location.getCoordinates().getLongitude()))
                .businessHours(new BusinessHoursDto(location.getBusinessHours().getOpensAt(), location.getBusinessHours().getClosesAt()))
                .evses(location.getEvses().stream()
                        .map(EVSEResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .lastUpdated(location.getLastUpdated())
                .build();
    }
}