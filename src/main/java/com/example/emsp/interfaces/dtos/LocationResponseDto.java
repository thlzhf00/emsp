package com.example.emsp.interfaces.dtos;

import com.example.emsp.domain.location.Location;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for responding with Location details.
 * Uses @Value for immutability and automatic getters/equals/hashCode/toString.
 * Uses @Builder for convenient object creation.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
@Builder // Generates a builder for constructing instances
public class LocationResponseDto {
    Long id;
    String name;
    String address;
    CoordinatesDto coordinates;
    BusinessHoursDto businessHours;
    LocalDateTime lastUpdated;

    /**
     * Converts a domain Location entity to a LocationResponseDto.
     *
     * @param location The Location entity.
     * @return A new LocationResponseDto instance.
     */
    public static LocationResponseDto fromEntity(Location location) {
        return LocationResponseDto.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .coordinates(new CoordinatesDto(location.getCoordinates().getLatitude(), location.getCoordinates().getLongitude()))
                .businessHours(new BusinessHoursDto(location.getBusinessHours().getOpensAt(), location.getBusinessHours().getClosesAt()))
                .lastUpdated(location.getLastUpdated())
                .build();
    }
}