package com.example.emsp.domain.events;

import com.example.emsp.domain.evse.EVSEStatus;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Domain event indicating that a new EVSE has been added to a Location.
 * This event carries essential information about the newly added EVSE.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class EVSEAddedEvent {
    Long evseId; // Internal DB ID
    String evseOcpiId; // OCPI-compliant EVSE ID
    Long locationId;
    EVSEStatus initialStatus;
    LocalDateTime occurredOn;

    /**
     * Constructor for EVSEAddedEvent.
     *
     * @param evseId        The internal database ID of the newly added EVSE.
     * @param evseOcpiId    The OCPI-compliant ID of the newly added EVSE.
     * @param locationId    The ID of the Location to which the EVSE was added.
     * @param initialStatus The initial status of the EVSE.
     */
    public EVSEAddedEvent(Long evseId, String evseOcpiId, Long locationId, EVSEStatus initialStatus) {
        this.evseId = evseId;
        this.evseOcpiId = evseOcpiId;
        this.locationId = locationId;
        this.initialStatus = initialStatus;
        this.occurredOn = LocalDateTime.now(); // Timestamp of when the event occurred
    }
}