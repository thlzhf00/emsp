package com.example.emsp.application;

import com.example.emsp.domain.evse.EVSEId;
import com.example.emsp.domain.evse.EVSERepository;
import com.example.emsp.domain.evse.EVSEStatus;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.domain.location.Location;
import com.example.emsp.domain.location.LocationRepository;
import com.example.emsp.infrastructure.exception.EVSEIdFormatException;
import com.example.emsp.infrastructure.exception.InvalidEVSEStatusTransitionException;
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
 * Unit tests for EVSEApplicationService.
 * Uses Mockito to mock EVSERepository and LocationRepository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class EVSEApplicationServiceTest {

    @Mock
    private EVSERepository evseRepository;
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private EVSEApplicationService evseApplicationService;

    private Location testLocation;
    private EVSE testEVSE;
    private final String validEvseIdValue = "US*ABC*EVSE123";

    @BeforeEach
    void setUp() {
        testLocation = mock(Location.class); // Mock Location for interaction

        // Initialize testEVSE as a real object for testing its internal logic (like changeStatus)
        // Its EVSEId value will be consistent with validEvseIdValue
        testEVSE = new EVSE(new EVSEId(validEvseIdValue), testLocation);
        try {
            // Set the ID for testEVSE
            java.lang.reflect.Field idField = testEVSE.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testEVSE, 1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set ID on test EVSE: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should successfully add EVSE to location with valid ID")
    void shouldAddEvseToLocationWithValidId() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(evseRepository.findByEvseIdText(validEvseIdValue)).thenReturn(Optional.empty());

        when(evseRepository.save(any(EVSE.class))).thenAnswer(invocation -> {
            EVSE evse = invocation.getArgument(0);
            try {
                // Use reflection to set the ID as it would be set by JPA
                java.lang.reflect.Field idField = evse.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(evse, 1L);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Failed to set ID on mocked EVSE: " + e.getMessage());
            }
            return evse;
        });

        EVSE createdEVSE = evseApplicationService.addEVSEToLocation(1L, validEvseIdValue);

        assertNotNull(createdEVSE);
        assertEquals(validEvseIdValue, createdEVSE.getEvseId().getText());
        assertEquals(EVSEStatus.AVAILABLE, createdEVSE.getStatus());
        verify(locationRepository, times(1)).findById(1L);
        verify(evseRepository, times(1)).findByEvseIdText(validEvseIdValue);
        verify(evseRepository, times(1)).save(any(EVSE.class));
        verify(testLocation, times(1)).addEVSE(any(EVSE.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when adding EVSE to non-existent location")
    void shouldThrowExceptionWhenAddingEvseToNonExistentLocation() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                evseApplicationService.addEVSEToLocation(99L, validEvseIdValue));

        verify(locationRepository, times(1)).findById(99L);
        verify(evseRepository, never()).save(any(EVSE.class));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException when adding EVSE with invalid ID format")
    void shouldThrowExceptionWhenAddingEvseWithInvalidIdFormat() {
        String invalidId = "US-ABC-EVSE123"; // Invalid format
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));

        assertThrows(EVSEIdFormatException.class, () ->
                evseApplicationService.addEVSEToLocation(1L, invalidId));

        verify(locationRepository, times(1)).findById(1L);
        verify(evseRepository, never()).findByEvseIdText(anyString());
        verify(evseRepository, never()).save(any(EVSE.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when adding EVSE with existing ID")
    void shouldThrowExceptionWhenAddingEvseWithExistingId() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(evseRepository.findByEvseIdText(validEvseIdValue)).thenReturn(Optional.of(testEVSE)); // Already exists

        assertThrows(IllegalStateException.class, () ->
                evseApplicationService.addEVSEToLocation(1L, validEvseIdValue));

        verify(locationRepository, times(1)).findById(1L);
        verify(evseRepository, times(1)).findByEvseIdText(validEvseIdValue);
        verify(evseRepository, never()).save(any(EVSE.class));
    }

    @Test
    @DisplayName("Should successfully change EVSE status to BLOCKED from AVAILABLE")
    void shouldChangeEvseStatusToBlocked() {
        // Ensure testEVSE starts as AVAILABLE
        assertEquals(EVSEStatus.AVAILABLE, testEVSE.getStatus());
        when(evseRepository.findByEvseIdText(validEvseIdValue)).thenReturn(Optional.of(testEVSE));

        when(evseRepository.save(any(EVSE.class))).thenAnswer(invocation -> {
            EVSE evse = invocation.getArgument(0);
            try {
                // Use reflection to set the ID as it would be set by JPA
                java.lang.reflect.Field idField = evse.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(evse, 1L);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Failed to set ID on mocked EVSE: " + e.getMessage());
            }
            return evse;
        });

        EVSE updatedEVSE = evseApplicationService.changeEVSEStatus(validEvseIdValue, EVSEStatus.BLOCKED);

        assertNotNull(updatedEVSE);
        assertEquals(EVSEStatus.BLOCKED, updatedEVSE.getStatus());
        verify(evseRepository, times(1)).findByEvseIdText(validEvseIdValue);
        verify(evseRepository, times(1)).save(testEVSE);
    }

    @Test
    @DisplayName("Should successfully change EVSE status to REMOVED from AVAILABLE")
    void shouldChangeEvseStatusToRemoved() {
        // Ensure testEVSE starts as AVAILABLE
        assertEquals(EVSEStatus.AVAILABLE, testEVSE.getStatus());
        when(evseRepository.findByEvseIdText(validEvseIdValue)).thenReturn(Optional.of(testEVSE));

        when(evseRepository.save(any(EVSE.class))).thenAnswer(invocation -> {
            EVSE evse = invocation.getArgument(0);
            try {
                // Use reflection to set the ID as it would be set by JPA
                java.lang.reflect.Field idField = evse.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(evse, 1L);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Failed to set ID on mocked EVSE: " + e.getMessage());
            }
            return evse;
        });

        EVSE updatedEVSE = evseApplicationService.changeEVSEStatus(validEvseIdValue, EVSEStatus.REMOVED);

        assertNotNull(updatedEVSE);
        assertEquals(EVSEStatus.REMOVED, updatedEVSE.getStatus());
        verify(evseRepository, times(1)).findByEvseIdText(validEvseIdValue);
        verify(evseRepository, times(1)).save(testEVSE);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when changing status of non-existent EVSE")
    void shouldThrowExceptionWhenChangingStatusOfNonExistentEvse() {
        String nonExistentEvseId = "US*XYZ*NONEXISTENT";
        when(evseRepository.findByEvseIdText(nonExistentEvseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                evseApplicationService.changeEVSEStatus(nonExistentEvseId, EVSEStatus.INOPERATIVE));

        verify(evseRepository, times(1)).findByEvseIdText(nonExistentEvseId);
        verify(evseRepository, never()).save(any(EVSE.class));
    }

    @Test
    @DisplayName("Should throw InvalidEVSEStatusTransitionException for invalid status transition")
    void shouldThrowExceptionForInvalidStatusTransition() {
        // Force EVSE to BLOCKED initially for this test
        testEVSE = new EVSE(new EVSEId(validEvseIdValue), testLocation);
        testEVSE.changeStatus(EVSEStatus.BLOCKED); // Manually change status to BLOCKED

        when(evseRepository.findByEvseIdText(validEvseIdValue)).thenReturn(Optional.of(testEVSE));

        assertThrows(InvalidEVSEStatusTransitionException.class, () ->
                evseApplicationService.changeEVSEStatus(validEvseIdValue, EVSEStatus.INOPERATIVE)); // BLOCKED to INOPERATIVE is invalid

        verify(evseRepository, times(1)).findByEvseIdText(validEvseIdValue);
        verify(evseRepository, never()).save(any(EVSE.class)); // Ensure save is not called
    }

    @Test
    @DisplayName("Should successfully find EVSE by EVSE ID value")
    void shouldFindEvseByEvseIdValue() {
        when(evseRepository.findByEvseIdText(validEvseIdValue)).thenReturn(Optional.of(testEVSE));

        Optional<EVSE> foundEVSE = evseApplicationService.findEVSEByEVSEIdValue(validEvseIdValue);

        assertTrue(foundEVSE.isPresent());
        assertEquals(validEvseIdValue, foundEVSE.get().getEvseId().getText());
        verify(evseRepository, times(1)).findByEvseIdText(validEvseIdValue);
    }

    @Test
    @DisplayName("Should return empty Optional when EVSE is not found by EVSE ID value")
    void shouldReturnEmptyOptionalWhenEvseNotFoundByEvseIdValue() {
        String nonExistentEvseId = "US*XYZ*NONEXISTENT";
        when(evseRepository.findByEvseIdText(nonExistentEvseId)).thenReturn(Optional.empty());

        Optional<EVSE> foundEVSE = evseApplicationService.findEVSEByEVSEIdValue(nonExistentEvseId);

        assertTrue(foundEVSE.isEmpty());
        verify(evseRepository, times(1)).findByEvseIdText(nonExistentEvseId);
    }
}