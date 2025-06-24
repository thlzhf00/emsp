package com.example.emsp.interfaces.controllers;

import com.example.emsp.application.ConnectorApplicationService;
import com.example.emsp.application.LocationApplicationService;
import com.example.emsp.domain.evse.EVSEId;
import com.example.emsp.domain.location.Connector;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.domain.location.Location;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import com.example.emsp.interfaces.dtos.ConnectorRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ConnectorController using MockMvc.
 * @WebMvcTest slices the Spring Boot context to only include web layer components.
 * MockBean is used to mock the ConnectorApplicationService.
 */
@WebMvcTest(ConnectorController.class)
class ConnectorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static ConnectorApplicationService connectorApplicationService;
    @TestConfiguration // Marks this as a configuration specific to tests
    static class TestConfig {
        @Bean
        public ConnectorApplicationService connectorApplicationService() {
            // Create and return the mock instance
            connectorApplicationService = Mockito.mock(ConnectorApplicationService.class);
            return connectorApplicationService;
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;
    private Connector mockConnector;
    private final String validEvseId = "US*ABC*EVSE123";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create mock EVSE and Location for the Connector
        Location mockLocation = new Location("Mock Loc", "Mock Addr", null, null);
        try {
            java.lang.reflect.Field idFieldLoc = mockLocation.getClass().getDeclaredField("id");
            idFieldLoc.setAccessible(true);
            idFieldLoc.set(mockLocation, 100L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        EVSE mockEvse = new EVSE(new EVSEId(validEvseId), mockLocation);
        try {
            java.lang.reflect.Field idFieldEvse = mockEvse.getClass().getDeclaredField("id");
            idFieldEvse.setAccessible(true);
            idFieldEvse.set(mockEvse, 200L); // Set a dummy ID for the EVSE
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        mockConnector = new Connector("Type2", 22.0, 400.0, mockEvse);
        try {
            // Set ID and lastUpdated for mockConnector
            java.lang.reflect.Field idField = mockConnector.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockConnector, 1L);

            java.lang.reflect.Field lastUpdatedField = mockConnector.getClass().getDeclaredField("lastUpdated");
            lastUpdatedField.setAccessible(true);
            lastUpdatedField.set(mockConnector, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("POST /api/v1/evses/{evseId}/connectors - Should add connector successfully")
    void addConnectorToEVSE_Success() throws Exception {
        ConnectorRequestDto requestDto = new ConnectorRequestDto("Type2", 22.0, 400.0);

        when(connectorApplicationService.addConnectorToEVSE(eq(validEvseId), any(), any(), any()))
                .thenReturn(mockConnector);

        mockMvc.perform(post("/api/v1/evses/{evseId}/connectors", validEvseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.standard").value("Type2"))
                .andExpect(jsonPath("$.powerLevel").value(22.0))
                .andExpect(jsonPath("$.voltage").value(400.0))
                .andExpect(jsonPath("$.evseId").value(200L)); // Checks that the parent EVSE ID is included
    }

    @Test
    @DisplayName("POST /api/v1/evses/{evseId}/connectors - Should return 400 for invalid request body")
    void addConnectorToEVSE_BadRequest() throws Exception {
        ConnectorRequestDto invalidRequest = new ConnectorRequestDto("", -10.0, 0.0); // Invalid standard, powerLevel, voltage

        mockMvc.perform(post("/api/v1/evses/{evseId}/connectors", validEvseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("POST /api/v1/evses/{evseId}/connectors - Should return 404 if EVSE not found")
    void addConnectorToEVSE_EvseNotFound() throws Exception {
        String nonExistentEvseId = "US*XYZ*NONEXISTENT";
        ConnectorRequestDto requestDto = new ConnectorRequestDto("Type2", 22.0, 400.0);

        when(connectorApplicationService.addConnectorToEVSE(eq(nonExistentEvseId), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("EVSE not found with EVSE ID: " + nonExistentEvseId));

        mockMvc.perform(post("/api/v1/evses/{evseId}/connectors", nonExistentEvseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }
}