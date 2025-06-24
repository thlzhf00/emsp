package com.example.emsp.domain.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Value Object representing business hours (opening and closing time).
 * This class is immutable and its equality is based on its attributes.
 * It is embeddable, meaning it can be directly embedded into an entity's table.
 */
@Embeddable // Marks this class as embeddable in other entities
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protected constructor for JPA
@AllArgsConstructor(access = AccessLevel.PUBLIC) // Public constructor for creation
public class BusinessHours {

    @NotNull(message = "Opening time cannot be null")
    private LocalTime opensAt;

    @NotNull(message = "Closing time cannot be null")
    private LocalTime closesAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessHours that = (BusinessHours) o;
        return Objects.equals(opensAt, that.opensAt) && Objects.equals(closesAt, that.closesAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opensAt, closesAt);
    }

    @Override
    public String toString() {
        return "BusinessHours{" +
                "opensAt=" + opensAt +
                ", closesAt=" + closesAt +
                '}';
    }
}