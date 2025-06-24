package com.example.emsp.application;

import com.example.emsp.domain.common.BusinessHours;
import com.example.emsp.domain.common.Coordinates;
import com.example.emsp.domain.location.Location;
import com.example.emsp.domain.location.LocationRepository;
import com.example.emsp.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LocationApplicationService.
 * Uses Mockito to mock the LocationRepository dependency.
 */
@ExtendWith(MockitoExtension.class)
class LocationApplicationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationApplicationService locationApplicationService;

    private Location testLocation;
    private Coordinates testCoordinates;
    private BusinessHours testBusinessHours;

    @BeforeEach
    void setUp() {
        testCoordinates = new Coordinates(34.0, -118.0);
        testBusinessHours = new BusinessHours(LocalTime.of(8, 0), LocalTime.of(22, 0));
        testLocation = new Location("Test Location", "123 Test St", testCoordinates, testBusinessHours);
    }

    @Test
    @DisplayName("Should successfully create a new location")
    void shouldCreateNewLocation() {
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location loc = invocation.getArgument(0);
            if (loc.getId() == null) {
                // Assign a dummy ID for testing purposes if not already set (for new entities)
                try {
                    java.lang.reflect.Field idField = loc.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(loc, 1L);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return loc;
        });
        Location createdLocation = locationApplicationService.createLocation(
                "New Location", "456 New St", new Coordinates(35.0, -110.0), new BusinessHours(LocalTime.of(7, 0), LocalTime.of(23, 0))
        );

        assertNotNull(createdLocation);
        assertEquals("New Location", createdLocation.getName());
        assertNotNull(createdLocation.getId());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    @DisplayName("Should successfully update an existing location")
    void shouldUpdateExistingLocation() {
        Long locationId = 1L;
        Location existingLocation = new Location("Old Name", "Old Address", testCoordinates, testBusinessHours);
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location loc = invocation.getArgument(0);
            if (loc.getId() == null) {
                // Assign a dummy ID for testing purposes if not already set (for new entities)
                try {
                    java.lang.reflect.Field idField = loc.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(loc, 1L);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return loc;
        });
        try {
            java.lang.reflect.Field idField = existingLocation.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(existingLocation, locationId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        when(locationRepository.findById(locationId)).thenReturn(Optional.of(existingLocation));

        Location updatedLocation = locationApplicationService.updateLocation(
                locationId,
                "Updated Name",
                "Updated Address",
                new Coordinates(36.0, -115.0),
                new BusinessHours(LocalTime.of(9, 0), LocalTime.of(21, 0))
        );

        assertNotNull(updatedLocation);
        assertEquals(locationId, updatedLocation.getId());
        assertEquals("Updated Name", updatedLocation.getName());
        assertEquals("Updated Address", updatedLocation.getAddress());
        assertEquals(36.0, updatedLocation.getCoordinates().getLatitude());
        assertEquals(LocalTime.of(9, 0), updatedLocation.getBusinessHours().getOpensAt());
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, times(1)).save(existingLocation);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent location")
    void shouldThrowExceptionWhenUpdatingNonExistentLocation() {
        Long nonExistentId = 99L;
        when(locationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                locationApplicationService.updateLocation(
                        nonExistentId, "Name", "Address", testCoordinates, testBusinessHours
                ));
        verify(locationRepository, times(1)).findById(nonExistentId);
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    @DisplayName("Should successfully find a location by ID")
    void shouldFindLocationById() {
        Long locationId = 1L;
        Location existingLocation = new Location("Found Location", "Found Address", testCoordinates, testBusinessHours);
        try {
            java.lang.reflect.Field idField = existingLocation.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(existingLocation, locationId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(existingLocation));

        Location foundLocation = locationApplicationService.findLocationById(locationId);

        assertNotNull(foundLocation);
        assertEquals(locationId, foundLocation.getId());
        assertEquals("Found Location", foundLocation.getName());
        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when finding non-existent location by ID")
    void shouldThrowExceptionWhenFindingNonExistentLocationById() {
        Long nonExistentId = 99L;
        when(locationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                locationApplicationService.findLocationById(nonExistentId));
        verify(locationRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should query locations by last updated timestamp with pagination")
    void shouldQueryLocationsByLastUpdated() {
        LocalDateTime timestamp = LocalDateTime.now().minusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        Location location1 = new Location("Loc1", "Addr1", testCoordinates, testBusinessHours);
        Location location2 = new Location("Loc2", "Addr2", testCoordinates, testBusinessHours);
        Page<Location> mockPage = new PageImpl<>(Arrays.asList(location1, location2), pageable, 2);

        when(locationRepository.findByLastUpdatedAfter(timestamp, pageable)).thenReturn(mockPage);

        Page<Location> resultPage = locationApplicationService.queryLocationsByLastUpdated(timestamp, pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        assertEquals(2, resultPage.getContent().size());
        verify(locationRepository, times(1)).findByLastUpdatedAfter(timestamp, pageable);
    }
}