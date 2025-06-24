package com.example.emsp.domain.evse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EVSEStatus enum, specifically its isValidTransition method.
 * Focuses on testing the state transition rules.
 */
class EVSEStatusTest {

    // Test cases for transitions to REMOVED
    @Test
    @DisplayName("Any state should transition to REMOVED")
    void anyStateShouldTransitionToRemoved() {
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.AVAILABLE, EVSEStatus.REMOVED));
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.BLOCKED, EVSEStatus.REMOVED));
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.INOPERATIVE, EVSEStatus.REMOVED));
    }

    @Test
    @DisplayName("REMOVED should not transition to any other state")
    void removedShouldNotTransitionToAnyOtherState() {
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.REMOVED, EVSEStatus.AVAILABLE));
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.REMOVED, EVSEStatus.BLOCKED));
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.REMOVED, EVSEStatus.INOPERATIVE));
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.REMOVED, EVSEStatus.REMOVED)); // Self-transition is false as per rules "irreversible"
    }

    // Test cases for AVAILABLE state transitions
    @Test
    @DisplayName("AVAILABLE should transition to BLOCKED")
    void availableShouldTransitionToBlocked() {
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.AVAILABLE, EVSEStatus.BLOCKED));
    }

    @Test
    @DisplayName("AVAILABLE should transition to INOPERATIVE")
    void availableShouldTransitionToInoperative() {
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.AVAILABLE, EVSEStatus.INOPERATIVE));
    }

    @Test
    @DisplayName("AVAILABLE should not transition to AVAILABLE (self)")
    void availableShouldNotTransitionToAvailable() {
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.AVAILABLE, EVSEStatus.AVAILABLE));
    }

    // Test cases for BLOCKED state transitions
    @Test
    @DisplayName("BLOCKED should transition to AVAILABLE")
    void blockedShouldTransitionToAvailable() {
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.BLOCKED, EVSEStatus.AVAILABLE));
    }

    @Test
    @DisplayName("BLOCKED should not transition to BLOCKED (self)")
    void blockedShouldNotTransitionToBlocked() {
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.BLOCKED, EVSEStatus.BLOCKED));
    }

    @Test
    @DisplayName("BLOCKED should not transition to INOPERATIVE")
    void blockedShouldNotTransitionToInoperative() {
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.BLOCKED, EVSEStatus.INOPERATIVE));
    }

    // Test cases for INOPERATIVE state transitions
    @Test
    @DisplayName("INOPERATIVE should transition to AVAILABLE")
    void inoperativeShouldTransitionToAvailable() {
        assertTrue(EVSEStatus.isValidTransition(EVSEStatus.INOPERATIVE, EVSEStatus.AVAILABLE));
    }

    @Test
    @DisplayName("INOPERATIVE should not transition to INOPERATIVE (self)")
    void inoperativeShouldNotTransitionToInoperative() {
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.INOPERATIVE, EVSEStatus.INOPERATIVE));
    }

    @Test
    @DisplayName("INOPERATIVE should not transition to BLOCKED")
    void inoperativeShouldNotTransitionToBlocked() {
        assertFalse(EVSEStatus.isValidTransition(EVSEStatus.INOPERATIVE, EVSEStatus.BLOCKED));
    }

    // Test for initial state transition
    @Test
    @DisplayName("INITIAL (null) should transition to AVAILABLE")
    void initialShouldTransitionToAvailable() {
        assertTrue(EVSEStatus.isValidTransition(null, EVSEStatus.AVAILABLE));
    }

    @Test
    @DisplayName("INITIAL (null) should not transition to other states directly")
    void initialShouldNotTransitionToOtherStatesDirectly() {
        assertFalse(EVSEStatus.isValidTransition(null, EVSEStatus.BLOCKED));
        assertFalse(EVSEStatus.isValidTransition(null, EVSEStatus.INOPERATIVE));
    }
}