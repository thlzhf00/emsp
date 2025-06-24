package com.example.emsp.interfaces.controllers;

import com.example.emsp.application.EVSEApplicationService;
import com.example.emsp.application.LocationApplicationService;
import com.example.emsp.domain.common.BusinessHours;
import com.example.emsp.domain.common.Coordinates;
import com.example.emsp.domain.location.Location;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import com.example.emsp.interfaces.dtos.BusinessHoursDto;
import com.example.emsp.interfaces.dtos.CoordinatesDto;
import com.example.emsp.interfaces.dtos.LocationRequestDto;
import com.example.emsp.interfaces.dtos.LocationUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for LocationController using MockMvc.
 * @WebMvcTest slices the Spring Boot context to only include web layer components.
 */
@WebMvcTest(LocationController.class)
class LocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static LocationApplicationService locationApplicationService;
    @TestConfiguration // Marks this as a configuration specific to tests
    static class TestConfig {
        @Bean
        public LocationApplicationService locationApplicationService() {
            // Create and return the mock instance
            locationApplicationService = Mockito.mock(LocationApplicationService.class);
            return locationApplicationService;
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;
    private Location mockLocation;
    private LocationRequestDto createRequestDto;
    private LocationUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDateTime serialization/deserialization

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Coordinates coords = new Coordinates(34.05, -118.25);
        BusinessHours hours = new BusinessHours(LocalTime.of(8, 0), LocalTime.of(22, 0));
        mockLocation = new Location("Test Location", "123 Main St", coords, hours);
        // Set a dummy ID and lastUpdated for the mocked entity
        try {
            java.lang.reflect.Field idField = mockLocation.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockLocation, 1L);

            java.lang.reflect.Field lastUpdatedField = mockLocation.getClass().getDeclaredField("lastUpdated");
            lastUpdatedField.setAccessible(true);
            lastUpdatedField.set(mockLocation, LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        createRequestDto = new LocationRequestDto(
                "New Charging Hub", "456 Oak Ave",
                new CoordinatesDto(34.0, -118.0), new BusinessHoursDto(LocalTime.of(7, 0), LocalTime.of(23, 0))
        );

        updateRequestDto = new LocationUpdateRequestDto(
                "Updated Charging Hub", "789 Pine Ln",
                new CoordinatesDto(35.0, -119.0), new BusinessHoursDto(LocalTime.of(6, 0), LocalTime.of(22, 0))
        );
    }

    @Test
    @DisplayName("POST /api/v1/locations - Should create a new location successfully")
    void createLocation_Success() throws Exception {
        when(locationApplicationService.createLocation(any(), any(), any(), any())).thenReturn(mockLocation);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Location"))
                .andExpect(jsonPath("$.address").value("123 Main St"));
    }

    @Test
    @DisplayName("POST /api/v1/locations - Should return 400 Bad Request for invalid request body")
    void createLocation_BadRequest() throws Exception {
        LocationRequestDto invalidRequest = new LocationRequestDto(
                "", "456 Oak Ave", // Empty name
                new CoordinatesDto(34.0, -118.0), new BusinessHoursDto(LocalTime.of(7, 0), LocalTime.of(23, 0))
        );

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("PUT /api/v1/locations/{locationId} - Should update an existing location successfully")
    void updateLocation_Success() throws Exception {
        when(locationApplicationService.updateLocation(eq(1L), any(), any(), any(), any())).thenReturn(mockLocation);

        mockMvc.perform(put("/api/v1/locations/{locationId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Location")); // mockLocation has "Test Location" name
    }

    @Test
    @DisplayName("PUT /api/v1/locations/{locationId} - Should return 404 Not Found for non-existent location")
    void updateLocation_NotFound() throws Exception {
        when(locationApplicationService.updateLocation(eq(99L), any(), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Location not found with ID: 99"));

        mockMvc.perform(put("/api/v1/locations/{locationId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("GET /api/v1/locations/{locationId} - Should retrieve a location by ID successfully")
    void getLocationById_Success() throws Exception {
        when(locationApplicationService.findLocationById(eq(1L))).thenReturn(mockLocation);

        mockMvc.perform(get("/api/v1/locations/{locationId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Location"));
    }

    @Test
    @DisplayName("GET /api/v1/locations/{locationId} - Should return 404 Not Found for non-existent location")
    void getLocationById_NotFound() throws Exception {
        when(locationApplicationService.findLocationById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Location not found with ID: 99"));

        mockMvc.perform(get("/api/v1/locations/{locationId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("GET /api/v1/locations - Should query locations by last updated timestamp with pagination")
    void queryLocations_Success() throws Exception {
        org.springframework.data.domain.Page<Location> mockPage = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(mockLocation));
        when(locationApplicationService.queryLocationsByLastUpdated(any(LocalDateTime.class), any()))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/locations")
                        .param("lastUpdated", "2023-01-01T00:00:00")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Location"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}