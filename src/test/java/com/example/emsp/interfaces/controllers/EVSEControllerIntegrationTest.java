package com.example.emsp.interfaces.controllers;

import com.example.emsp.application.EVSEApplicationService;
import com.example.emsp.domain.evse.EVSEStatus;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.domain.location.Location;
import com.example.emsp.infrastructure.exception.EVSEIdFormatException;
import com.example.emsp.infrastructure.exception.InvalidEVSEStatusTransitionException;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import com.example.emsp.interfaces.dtos.EVSECreateRequestDto;
import com.example.emsp.interfaces.dtos.EVSEStatusUpdateRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for EVSEController using MockMvc.
 * @WebMvcTest slices the Spring Boot context to only include web layer components.
 * MockBean is used to mock the EVSEApplicationService.
 */
@WebMvcTest(EVSEController.class)
class EVSEControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static EVSEApplicationService evseApplicationService;
    @TestConfiguration // Marks this as a configuration specific to tests
    static class TestConfig {
        @Bean
        public EVSEApplicationService evseApplicationService() {
            // Create and return the mock instance
            evseApplicationService = Mockito.mock(EVSEApplicationService.class);
            return evseApplicationService;
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;
    private EVSE mockEVSE;
    private final String validEvseId = "US*ABC*EVSE123";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create a mock EVSE for successful responses
        Location mockLocation = new Location("Mock Loc", "Mock Addr", null, null);
        try {
            // Use reflection to set ID for mockLocation (as it's used in EVSEResponseDto.fromEntity)
            java.lang.reflect.Field idFieldLoc = mockLocation.getClass().getDeclaredField("id");
            idFieldLoc.setAccessible(true);
            idFieldLoc.set(mockLocation, 100L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        mockEVSE = new EVSE(new com.example.emsp.domain.evse.EVSEId(validEvseId), mockLocation);
        try {
            // Set ID and lastUpdated for mockEVSE
            java.lang.reflect.Field idField = mockEVSE.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockEVSE, 1L);

            java.lang.reflect.Field lastUpdatedField = mockEVSE.getClass().getDeclaredField("lastUpdated");
            lastUpdatedField.setAccessible(true);
            lastUpdatedField.set(mockEVSE, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("POST /api/v1/locations/{locationId}/evses - Should add EVSE successfully")
    void addEVSEToLocation_Success() throws Exception {
        EVSECreateRequestDto requestDto = new EVSECreateRequestDto(validEvseId, EVSEStatus.AVAILABLE);

        when(evseApplicationService.addEVSEToLocation(eq(1L), eq(validEvseId))).thenReturn(mockEVSE);

        mockMvc.perform(post("/api/v1/locations/{locationId}/evses", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.evseId").value(validEvseId))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("POST /api/v1/locations/{locationId}/evses - Should return 400 for invalid EVSE ID format")
    void addEVSEToLocation_InvalidEvseIdFormat() throws Exception {
        String invalidEvseId = "INVALID_ID";
        EVSECreateRequestDto requestDto = new EVSECreateRequestDto(invalidEvseId, EVSEStatus.AVAILABLE);

        // Mock the application service to throw the expected exception
        when(evseApplicationService.addEVSEToLocation(eq(1L), eq(invalidEvseId)))
                .thenThrow(new EVSEIdFormatException("Invalid EVSE ID format: " + invalidEvseId));

        mockMvc.perform(post("/api/v1/locations/{locationId}/evses", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid EVSE ID Format"));
    }

    @Test
    @DisplayName("POST /api/v1/locations/{locationId}/evses - Should return 404 if location not found")
    void addEVSEToLocation_LocationNotFound() throws Exception {
        EVSECreateRequestDto requestDto = new EVSECreateRequestDto(validEvseId, EVSEStatus.AVAILABLE);

        when(evseApplicationService.addEVSEToLocation(eq(99L), eq(validEvseId)))
                .thenThrow(new ResourceNotFoundException("Location not found with ID: 99"));

        mockMvc.perform(post("/api/v1/locations/{locationId}/evses", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("PATCH /api/v1/evses/{evseId}/status - Should change EVSE status successfully")
    void changeEVSEStatus_Success() throws Exception {
        EVSEStatusUpdateRequestDto requestDto = new EVSEStatusUpdateRequestDto(EVSEStatus.BLOCKED);

        // Ensure mockEVSE reflects the change after the mock service call
        mockEVSE.changeStatus(EVSEStatus.BLOCKED); // Update the mock object's status
        when(evseApplicationService.changeEVSEStatus(eq(validEvseId), eq(EVSEStatus.BLOCKED)))
                .thenReturn(mockEVSE);

        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", validEvseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evseId").value(validEvseId))
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    @DisplayName("PATCH /api/v1/evses/{evseId}/status - Should return 404 if EVSE not found")
    void changeEVSEStatus_EvseNotFound() throws Exception {
        String nonExistentEvseId = "US*XYZ*NONEXISTENT";
        EVSEStatusUpdateRequestDto requestDto = new EVSEStatusUpdateRequestDto(EVSEStatus.INOPERATIVE);

        when(evseApplicationService.changeEVSEStatus(eq(nonExistentEvseId), eq(EVSEStatus.INOPERATIVE)))
                .thenThrow(new ResourceNotFoundException("EVSE not found with EVSE ID: " + nonExistentEvseId));

        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", nonExistentEvseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("PATCH /api/v1/evses/{evseId}/status - Should return 409 for invalid status transition")
    void changeEVSEStatus_InvalidTransition() throws Exception {
        EVSEStatusUpdateRequestDto requestDto = new EVSEStatusUpdateRequestDto(EVSEStatus.INOPERATIVE);

        // Simulate current status to be BLOCKED, so BLOCKED -> INOPERATIVE is invalid
        when(evseApplicationService.changeEVSEStatus(eq(validEvseId), eq(EVSEStatus.INOPERATIVE)))
                .thenThrow(new InvalidEVSEStatusTransitionException(
                        "Invalid EVSE status transition from BLOCKED to INOPERATIVE for EVSE ID " + validEvseId));

        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", validEvseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Invalid EVSE Status Transition"));
    }
}