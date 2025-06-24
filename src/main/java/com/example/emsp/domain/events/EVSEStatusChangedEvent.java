package com.example.emsp.domain.events;

import com.example.emsp.domain.evse.EVSEStatus;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Domain event indicating that an EVSE's status has changed.
 * This event carries information about the EVSE and its status transition.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class EVSEStatusChangedEvent {
    Long evseId; // Internal DB ID
    String evseOcpiId; // OCPI-compliant EVSE ID
    EVSEStatus oldStatus;
    EVSEStatus newStatus;
    LocalDateTime occurredOn;

    /**
     * Constructor for EVSEStatusChangedEvent.
     *
     * @param evseId       The internal database ID of the EVSE whose status changed.
     * @param evseOcpiId   The OCPI-compliant ID of the EVSE.
     * @param oldStatus    The previous status of the EVSE.
     * @param newStatus    The new status of the EVSE.
     */
    public EVSEStatusChangedEvent(Long evseId, String evseOcpiId, EVSEStatus oldStatus, EVSEStatus newStatus) {
        this.evseId = evseId;
        this.evseOcpiId = evseOcpiId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.occurredOn = LocalDateTime.now(); // Timestamp of when the event occurred
    }
}