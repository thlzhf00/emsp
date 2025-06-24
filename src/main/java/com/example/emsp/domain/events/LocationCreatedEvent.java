package com.example.emsp.domain.events;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Domain event indicating that a new Location has been created.
 * This event carries essential information about the newly created location.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class LocationCreatedEvent {
    Long locationId;
    String locationName;
    String locationAddress;
    LocalDateTime occurredOn;

    /**
     * Constructor for LocationCreatedEvent.
     *
     * @param locationId      The ID of the newly created location.
     * @param locationName    The name of the newly created location.
     * @param locationAddress The address of the newly created location.
     */
    public LocationCreatedEvent(Long locationId, String locationName, String locationAddress) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.occurredOn = LocalDateTime.now(); // Timestamp of when the event occurred
    }
}