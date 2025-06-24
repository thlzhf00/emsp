package com.example.emsp.application;

import com.example.emsp.domain.evse.EVSEId;
import com.example.emsp.domain.evse.EVSERepository;
import com.example.emsp.domain.evse.EVSEStatus;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.domain.location.Location;
import com.example.emsp.domain.location.LocationRepository;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application Service for managing EVSE entities.
 * This service orchestrates domain logic and interacts with repositories.
 * It handles transactions and ensures data consistency.
 */
@Service
@RequiredArgsConstructor
public class EVSEApplicationService {

    private final EVSERepository evseRepository;
    private final LocationRepository locationRepository;

    /**
     * Adds a new EVSE to a specific Location.
     * Validates the EVSE ID format during creation.
     *
     * @param locationId The ID of the Location to which the EVSE will be added.
     * @param evseIdText The string value of the EVSE ID.
     * @return The created EVSE entity.
     * @throws ResourceNotFoundException if the Location is not found.
     * @throws com.emsp.infrastructure.exception.EVSEIdFormatException if the EVSE ID format is invalid.
     * @throws IllegalStateException if an EVSE with the given EVSE ID already exists.
     */
    @Transactional
    public EVSE addEVSEToLocation(Long locationId, String evseIdText) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));

        // Create EVSEId value object, which includes format validation
        EVSEId evseId = new EVSEId(evseIdText);

        // Check if an EVSE with this ID already exists to enforce uniqueness
        if (evseRepository.findByEvseIdText(evseIdText).isPresent()) {
            throw new IllegalStateException("EVSE with ID " + evseIdText + " already exists.");
        }

        EVSE evse = new EVSE(evseId, location);
        location.addEVSE(evse); // Add EVSE to location's collection (managed by cascade)
        return evseRepository.save(evse);
    }

    /**
     * Changes the status of an existing EVSE, enforcing state transition rules.
     *
     * @param evseIdText The string value of the EVSE ID to update.
     * @param newStatus   The desired new status.
     * @return The updated EVSE entity.
     * @throws ResourceNotFoundException if the EVSE is not found.
     * @throws com.emsp.infrastructure.exception.InvalidEVSEStatusTransitionException if the status transition is invalid.
     */
    @Transactional
    public EVSE changeEVSEStatus(String evseIdText, EVSEStatus newStatus) {
        EVSE evse = evseRepository.findByEvseIdText(evseIdText)
                .orElseThrow(() -> new ResourceNotFoundException("EVSE not found with EVSE ID: " + evseIdText));

        evse.changeStatus(newStatus); // This method contains the domain logic for state transitions
        return evseRepository.save(evse);
    }

    /**
     * Finds an EVSE by its EVSE ID value.
     *
     * @param evseIdText The string value of the EVSE ID.
     * @return An Optional containing the EVSE if found.
     */
    public Optional<EVSE> findEVSEByEVSEIdValue(String evseIdText) {
        return evseRepository.findByEvseIdText(evseIdText);
    }
}