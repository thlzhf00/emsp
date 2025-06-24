package com.example.emsp.application;

import com.example.emsp.domain.evse.EVSERepository;
import com.example.emsp.domain.location.Connector;
import com.example.emsp.domain.location.ConnectorRepository;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConnectorApplicationService.
 * Uses Mockito to mock ConnectorRepository and EVSERepository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class ConnectorApplicationServiceTest {

    @Mock
    private ConnectorRepository connectorRepository;
    @Mock
    private EVSERepository evseRepository;

    @InjectMocks
    private ConnectorApplicationService connectorApplicationService;

    private EVSE testEVSE;
    private final String testEvseIdValue = "US*ABC*EVSE123";

    @BeforeEach
    void setUp() {
        // Mock EVSE to simulate its behavior (e.g., addConnector)
        testEVSE = mock(EVSE.class);
    }

    @Test
    @DisplayName("Should successfully add a connector to an existing EVSE")
    void shouldAddConnectorToExistingEvse() {
        when(evseRepository.findByEvseIdText(testEvseIdValue)).thenReturn(Optional.of(testEVSE));

        when(connectorRepository.save(any(Connector.class))).thenAnswer(invocation -> {
            Connector connector = invocation.getArgument(0);
            try {
                // Use reflection to set the ID as it would be set by JPA
                java.lang.reflect.Field idField = connector.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(connector, 1L); // Assign a dummy ID
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Failed to set ID on mocked Connector: " + e.getMessage());
            }
            return connector;
        });

        Connector createdConnector = connectorApplicationService.addConnectorToEVSE(
                testEvseIdValue, "Type2", 22.0, 400.0);

        assertNotNull(createdConnector);
        assertEquals("Type2", createdConnector.getStandard());
        assertEquals(22.0, createdConnector.getPowerLevel());
        assertEquals(400.0, createdConnector.getVoltage());
        // Verify that the connector's EVSE reference is set correctly
        assertEquals(testEVSE, createdConnector.getEvse());

        verify(evseRepository, times(1)).findByEvseIdText(testEvseIdValue);
        verify(testEVSE, times(1)).addConnector(any(Connector.class)); // Verify EVSE's domain method called
        verify(connectorRepository, times(1)).save(any(Connector.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when adding connector to non-existent EVSE")
    void shouldThrowExceptionWhenAddingConnectorToNonExistentEvse() {
        String nonExistentEvseId = "US*XYZ*NONEXISTENT";
        when(evseRepository.findByEvseIdText(nonExistentEvseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                connectorApplicationService.addConnectorToEVSE(nonExistentEvseId, "Type2", 22.0, 400.0));

        verify(evseRepository, times(1)).findByEvseIdText(nonExistentEvseId);
        verify(connectorRepository, never()).save(any(Connector.class));
        verify(testEVSE, never()).addConnector(any(Connector.class)); // Ensure EVSE's method is not called
    }
}