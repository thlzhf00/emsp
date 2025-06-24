package com.example.emsp.domain.events;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Domain event indicating that an existing Location has been updated.
 * This event carries essential information about the updated location.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class LocationUpdatedEvent {
    Long locationId;
    String newLocationName;
    String newLocationAddress;
    LocalDateTime occurredOn;

    /**
     * Constructor for LocationUpdatedEvent.
     *
     * @param locationId        The ID of the updated location.
     * @param newLocationName   The new name of the location.
     * @param newLocationAddress The new address of the location.
     */
    public LocationUpdatedEvent(Long locationId, String newLocationName, String newLocationAddress) {
        this.locationId = locationId;
        this.newLocationName = newLocationName;
        this.newLocationAddress = newLocationAddress;
        this.occurredOn = LocalDateTime.now(); // Timestamp of when the event occurred
    }
}