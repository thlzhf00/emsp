package com.example.emsp.application;

import com.example.emsp.domain.evse.EVSERepository;
import com.example.emsp.domain.location.Connector;
import com.example.emsp.domain.location.ConnectorRepository;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application Service for managing Connector entities.
 * This service orchestrates domain logic and interacts with repositories.
 * It handles transactions and ensures data consistency.
 */
@Service
@RequiredArgsConstructor
public class ConnectorApplicationService {

    private final ConnectorRepository connectorRepository;
    private final EVSERepository evseRepository;

    /**
     * Adds a new Connector to a specific EVSE.
     *
     * @param evseIdText The string value of the EVSE ID to which the connector will be added.
     * @param standard    The technical standard of the connector.
     * @param powerLevel  The power level (in kW) of the connector.
     * @param voltage     The voltage (in Volts) of the connector.
     * @return The created Connector entity.
     * @throws ResourceNotFoundException if the EVSE is not found.
     */
    @Transactional
    public Connector addConnectorToEVSE(String evseIdText, String standard, Double powerLevel, Double voltage) {
        EVSE evse = evseRepository.findByEvseIdText(evseIdText)
                .orElseThrow(() -> new ResourceNotFoundException("EVSE not found with EVSE ID(CI Trigger): " + evseIdText));

        Connector connector = new Connector(standard, powerLevel, voltage, evse);
        evse.addConnector(connector); // Add connector to EVSE's collection (managed by cascade)
        return connectorRepository.save(connector);
    }
}
