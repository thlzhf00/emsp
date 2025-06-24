package com.example.emsp.domain.events;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Domain event indicating that a new Connector has been added to an EVSE.
 * This event carries essential information about the newly added Connector.
 */
@Value // Generates immutable class with getters, equals, hashCode, toString
public class ConnectorAddedEvent {
    Long connectorId;
    Long evseId; // Internal DB ID of the parent EVSE
    String evseOcpiId; // OCPI-compliant EVSE ID of the parent EVSE
    String standard;
    Double powerLevel;
    Double voltage;
    LocalDateTime occurredOn;

    /**
     * Constructor for ConnectorAddedEvent.
     *
     * @param connectorId  The internal database ID of the newly added Connector.
     * @param evseId       The internal database ID of the parent EVSE.
     * @param evseOcpiId   The OCPI-compliant ID of the parent EVSE.
     * @param standard     The standard of the connector.
     * @param powerLevel   The power level of the connector.
     * @param voltage      The voltage of the connector.
     */
    public ConnectorAddedEvent(Long connectorId, Long evseId, String evseOcpiId, String standard, Double powerLevel, Double voltage) {
        this.connectorId = connectorId;
        this.evseId = evseId;
        this.evseOcpiId = evseOcpiId;
        this.standard = standard;
        this.powerLevel = powerLevel;
        this.voltage = voltage;
        this.occurredOn = LocalDateTime.now(); // Timestamp of when the event occurred
    }
}