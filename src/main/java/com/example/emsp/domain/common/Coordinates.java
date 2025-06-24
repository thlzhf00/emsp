package com.example.emsp.domain.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;

/**
 * Value Object representing geographical coordinates (latitude and longitude).
 * This class is immutable and its equality is based on its attributes.
 * It is embeddable, meaning it can be directly embedded into an entity's table.
 */
@Embeddable // Marks this class as embeddable in other entities
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protected constructor for JPA
@AllArgsConstructor(access = AccessLevel.PUBLIC) // Public constructor for creation
public class Coordinates {

    // Latitude ranges from -90 to +90
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    // Longitude ranges from -180 to +180
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}