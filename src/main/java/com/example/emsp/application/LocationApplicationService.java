package com.example.emsp.application;

import com.example.emsp.domain.common.BusinessHours;
import com.example.emsp.domain.common.Coordinates;
import com.example.emsp.domain.location.Location;
import com.example.emsp.domain.location.LocationRepository;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Application Service for managing Location entities.
 * This service orchestrates domain logic and interacts with repositories.
 * It handles transactions and ensures data consistency.
 */
@Service
@RequiredArgsConstructor
public class LocationApplicationService {

    private final LocationRepository locationRepository;

    /**
     * Creates a new Location.
     *
     * @param name          The name of the location.
     * @param address       The address of the location.
     * @param coordinates   The geographical coordinates.
     * @param businessHours The business hours.
     * @return The created Location entity.
     */
    @Transactional
    public Location createLocation(String name, String address, Coordinates coordinates, BusinessHours businessHours) {
        Location location = new Location(name, address, coordinates, businessHours);
        return locationRepository.save(location);
    }

    /**
     * Updates an existing Location.
     *
     * @param locationId    The ID of the location to update.
     * @param name          The new name.
     * @param address       The new address.
     * @param coordinates   The new coordinates.
     * @param businessHours The new business hours.
     * @return The updated Location entity.
     * @throws ResourceNotFoundException if the location with the given ID is not found.
     */
    @Transactional
    public Location updateLocation(Long locationId, String name, String address, Coordinates coordinates, BusinessHours businessHours) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));

        location.update(name, address, coordinates, businessHours);
        return locationRepository.save(location); // Save updated entity
    }

    /**
     * Finds a Location by its ID.
     *
     * @param locationId The ID of the location.
     * @return The found Location entity.
     * @throws ResourceNotFoundException if the location with the given ID is not found.
     */
    public Location findLocationById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));
    }

    /**
     * Queries locations that were last updated after a specific timestamp, with pagination.
     *
     * @param lastUpdated The timestamp to filter locations by.
     * @param pageable    The pagination information (page number, page size, sort).
     * @return A Page of Location entities.
     */
    public Page<Location> queryLocationsByLastUpdated(LocalDateTime lastUpdated, Pageable pageable) {
        return locationRepository.findByLastUpdatedAfter(lastUpdated, pageable);
    }
}