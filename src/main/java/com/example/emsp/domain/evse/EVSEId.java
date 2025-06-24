package com.example.emsp.domain.evse;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.emsp.infrastructure.exception.EVSEIdFormatException;

import java.util.Objects;

/**
 * Value Object representing the unique identifier for an EVSE.
 * This ID must comply with the OCPI-compliant EVSE ID format: <CountryCode>*<PartyID>*<LocalEVSEID>
 * Validation is performed during object construction.
 * This class is immutable and its equality is based on its string value.
 * It is embeddable, meaning it can be directly embedded into an entity's table.
 */
@Embeddable // Marks this class as embeddable in other entities
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protected constructor for JPA
public class EVSEId {

    // Regex for EVSE ID format: <CountryCode>*<PartyID>*<LocalEVSEID>
    // CountryCode: 2-letter ISO 3166 alpha-2 code (e.g., "US", "NL", "CN")
    // PartyID: 3-character alphanumeric code (e.g., "ABC")
    // LocalEVSEID: unique string (up to 30 characters) scoped within the operator
    private static final String EVSE_ID_REGEX = "^[A-Z]{2}\\*[A-Z0-9]{3}\\*[A-Za-z0-9_-]{1,30}$";

    @Pattern(regexp = EVSE_ID_REGEX, message = "EVSE ID does not match OCPI format: CountryCode*PartyID*LocalEVSEID")
    private String text;

    /**
     * Constructs an EVSEId object and validates its format.
     *
     * @param text The string representation of the EVSE ID.
     * @throws EVSEIdFormatException if the provided value does not match the OCPI format.
     */
    public EVSEId(String text) {
        if (text == null || !text.matches(EVSE_ID_REGEX)) {
            throw new EVSEIdFormatException("Invalid EVSE ID format: " + text + ". Expected format: CC*PID*LocalID (e.g., US*ABC*EVSE123).");
        }
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EVSEId evseId = (EVSEId) o;
        return Objects.equals(text, evseId.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return text;
    }
}