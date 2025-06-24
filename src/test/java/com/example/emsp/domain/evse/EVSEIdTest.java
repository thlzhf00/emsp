package com.example.emsp.domain.evse;

import com.example.emsp.infrastructure.exception.EVSEIdFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EVSEId Value Object.
 * Focuses on testing the validation logic during construction.
 */
class EVSEIdTest {

    @Test
    @DisplayName("Should create EVSEId with valid format")
    void shouldCreateEVSEIdWithValidFormat() {
        String validId = "US*ABC*EVSE123";
        EVSEId evseId = new EVSEId(validId);
        assertNotNull(evseId);
        assertEquals(validId, evseId.getText());
    }

    @Test
    @DisplayName("Should create EVSEId with maximum LocalEVSEID length")
    void shouldCreateEVSEIdWithMaxLocalEvseIdLength() {
        String longId = "NL*XYZ*123456789012345678901234567890"; // 30 characters
        EVSEId evseId = new EVSEId(longId);
        assertNotNull(evseId);
        assertEquals(longId, evseId.getText());
    }

    @Test
    @DisplayName("Should create EVSEId with alphanumeric PartyID and LocalEVSEID with hyphen/underscore")
    void shouldCreateEVSEIdWithAlphanumericAndSpecialChars() {
        String mixedId = "CN*123*Local_EVSE-ID_001";
        EVSEId evseId = new EVSEId(mixedId);
        assertNotNull(evseId);
        assertEquals(mixedId, evseId.getText());
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for null value")
    void shouldThrowExceptionForNullValue() {
        EVSEIdFormatException exception = assertThrows(EVSEIdFormatException.class, () -> new EVSEId(null));
        assertTrue(exception.getMessage().contains("Invalid EVSE ID format"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for empty string")
    void shouldThrowExceptionForEmptyString() {
        EVSEIdFormatException exception = assertThrows(EVSEIdFormatException.class, () -> new EVSEId(""));
        assertTrue(exception.getMessage().contains("Invalid EVSE ID format"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for missing parts")
    void shouldThrowExceptionForMissingParts() {
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*ABC"));
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US"));
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*ABC*"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for invalid CountryCode length")
    void shouldThrowExceptionForInvalidCountryCodeLength() {
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("U*ABC*EVSE123"));
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("USA*ABC*EVSE123"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for invalid CountryCode characters")
    void shouldThrowExceptionForInvalidCountryCodeChars() {
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("Us*ABC*EVSE123")); // Lowercase
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("U1*ABC*EVSE123"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for invalid PartyID length")
    void shouldThrowExceptionForInvalidPartyIdLength() {
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*AB*EVSE123"));
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*ABCD*EVSE123"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for invalid PartyID characters")
    void shouldThrowExceptionForInvalidPartyIdChars() {
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*AB!*EVSE123"));
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*abc*EVSE123")); // Lowercase
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for LocalEVSEID exceeding max length")
    void shouldThrowExceptionForLocalEvseIdExceedingMaxLength() {
        String tooLongId = "US*ABC*1234567890123456789012345678901"; // 31 characters
        EVSEIdFormatException exception = assertThrows(EVSEIdFormatException.class, () -> new EVSEId(tooLongId));
        assertTrue(exception.getMessage().contains("Invalid EVSE ID format"));
    }

    @Test
    @DisplayName("Should throw EVSEIdFormatException for LocalEVSEID with invalid characters")
    void shouldThrowExceptionForLocalEvseIdWithInvalidChars() {
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*ABC*EVSE!123"));
        assertThrows(EVSEIdFormatException.class, () -> new EVSEId("US*ABC*EVSE 123")); // Space
    }

    @Test
    @DisplayName("Should return correct value when toString is called")
    void shouldReturnCorrectValueWhenToStringIsCalled() {
        String id = "DE*789*ChargerXYZ";
        EVSEId evseId = new EVSEId(id);
        assertEquals(id, evseId.toString());
    }

    @Test
    @DisplayName("EVSEId objects with same value should be equal")
    void shouldBeEqualForSameValue() {
        EVSEId id1 = new EVSEId("US*ABC*EVSE1");
        EVSEId id2 = new EVSEId("US*ABC*EVSE1");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("EVSEId objects with different value should not be equal")
    void shouldNotBeEqualForDifferentValue() {
        EVSEId id1 = new EVSEId("US*ABC*EVSE1");
        EVSEId id2 = new EVSEId("US*ABC*EVSE2");
        assertNotEquals(id1, id2);
    }
}