package com.example.emsp.application.eventhandlers;

import com.example.emsp.domain.events.*; // Import all domain events
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component responsible for listening to and handling domain events.
 * This class demonstrates how different domain events can be consumed
 * and acted upon. In a real application, these might trigger side effects,
 * update read models, send notifications, or publish integration events.
 */
@Component
public class DomainEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(DomainEventHandler.class);

    /**
     * Listens for LocationCreatedEvent.
     *
     * @param event The LocationCreatedEvent that occurred.
     */
    @EventListener
    public void handleLocationCreated(LocationCreatedEvent event) {
        logger.info("Received LocationCreatedEvent: Location ID: {}, Name: {}, Address: {}, Occurred On: {}",
                event.getLocationId(), event.getLocationName(), event.getLocationAddress(), event.getOccurredOn());
        // Example: Send a notification, update a search index, log for analytics
        // We can use a dedicated message queue (e.g., Kafka, RabbitMQ) for more complex scenarios
    }

    /**
     * Listens for LocationUpdatedEvent.
     *
     * @param event The LocationUpdatedEvent that occurred.
     */
    @EventListener
    public void handleLocationUpdated(LocationUpdatedEvent event) {
        logger.info("Received LocationUpdatedEvent: Location ID: {}, New Name: {}, New Address: {}, Occurred On: {}",
                event.getLocationId(), event.getNewLocationName(), event.getNewLocationAddress(), event.getOccurredOn());
        // Example: Trigger synchronization with external mapping services
    }

    /**
     * Listens for EVSEAddedEvent.
     *
     * @param event The EVSEAddedEvent that occurred.
     */
    @EventListener
    public void handleEVSEAdded(EVSEAddedEvent event) {
        logger.info("Received EVSEAddedEvent: EVSE ID: {} (OCPI: {}), Location ID: {}, Initial Status: {}, Occurred On: {}",
                event.getEvseId(), event.getEvseOcpiId(), event.getLocationId(), event.getInitialStatus(), event.getOccurredOn());
        // Example: Update charging station availability dashboard
    }

    /**
     * Listens for EVSEStatusChangedEvent.
     *
     * @param event The EVSEStatusChangedEvent that occurred.
     */
    @EventListener
    public void handleEVSEStatusChanged(EVSEStatusChangedEvent event) {
        logger.info("Received EVSEStatusChangedEvent: EVSE ID: {} (OCPI: {}), Status Changed from {} to {}, Occurred On: {}",
                event.getEvseId(), event.getEvseOcpiId(), event.getOldStatus(), event.getNewStatus(), event.getOccurredOn());
        // Example: Send alerts for INOPERATIVE status, update mobile app UI in real-time
    }

    /**
     * Listens for ConnectorAddedEvent.
     *
     * @param event The ConnectorAddedEvent that occurred.
     */
    @EventListener
    public void handleConnectorAdded(ConnectorAddedEvent event) {
        logger.info("Received ConnectorAddedEvent: Connector ID: {}, EVSE ID: {} (OCPI: {}), Standard: {}, Power: {}kW, Voltage: {}V, Occurred On: {}",
                event.getConnectorId(), event.getEvseId(), event.getEvseOcpiId(), event.getStandard(), event.getPowerLevel(), event.getVoltage(), event.getOccurredOn());
        // Example: Update charging station technical specification database for billing or compatibility checks
    }
}