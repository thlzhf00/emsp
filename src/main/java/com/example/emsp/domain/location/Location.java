package com.example.emsp.domain.location;

import com.example.emsp.domain.common.BusinessHours;
import com.example.emsp.domain.common.Coordinates;
import com.example.emsp.domain.events.LocationCreatedEvent;
import com.example.emsp.domain.events.LocationUpdatedEvent;
import com.example.emsp.domain.evse.EVSE;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity representing a charging site (Location).
 * This is the Aggregate Root for Location, EVSE, and Connector entities.
 * It contains multiple EVSEs, and its lifecycle manages its associated EVSEs and Connectors.
 */
@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protected constructor for JPA
@EntityListeners(AuditingEntityListener.class) // For automatic 'lastUpdated'
public class Location extends AbstractAggregateRoot<Location> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location name cannot be blank")
    private String name;

    @NotBlank(message = "Location address cannot be blank")
    private String address;

    @Embedded
    @Valid // Validates the embedded Coordinates value object
    @NotNull(message = "Coordinates cannot be null")
    private Coordinates coordinates;

    @Embedded
    @Valid // Validates the embedded BusinessHours value object
    @NotNull(message = "Business hours cannot be null")
    private BusinessHours businessHours;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EVSE> evses = new ArrayList<>();

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated; // Automatically managed by JPA Auditing

    /**
     * Constructor for creating a new Location.
     *
     * @param name          The name of the location.
     * @param address       The address of the location.
     * @param coordinates   The geographical coordinates of the location.
     * @param businessHours The business hours of the location.
     */
    public Location(String name, String address, Coordinates coordinates, BusinessHours businessHours) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
        this.businessHours = businessHours;
        // lastUpdated will be set by @EnableJpaAuditing on persist

        // Register the event. This will be published after transaction commit.
        registerEvent(new LocationCreatedEvent(this.id, this.name, this.address));
    }

    /**
     * Updates the information of an existing Location.
     *
     * @param name          The new name.
     * @param address       The new address.
     * @param coordinates   The new coordinates.
     * @param businessHours The new business hours.
     */
    public void update(String name, String address, Coordinates coordinates, BusinessHours businessHours) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
        this.businessHours = businessHours;
        // lastUpdated will be updated by @EnableJpaAuditing

        // Register the event. This will be published after transaction commit.
        registerEvent(new LocationUpdatedEvent(this.id, this.name, this.address));
    }

    /**
     * Adds a new EVSE to this Location.
     *
     * @param evse The EVSE to add.
     */
    public void addEVSE(EVSE evse) {
        if (evse != null) {
            this.evses.add(evse);
            evse.setLocation(this); // Set bidirectional relationship
        }
    }

    /**
     * Removes an EVSE from this Location.
     *
     * @param evse The EVSE to remove.
     */
    public void removeEVSE(EVSE evse) {
        if (evse != null) {
            this.evses.remove(evse);
            evse.setLocation(null); // Remove bidirectional relationship
        }
    }

    /**
     * Returns an unmodifiable list of EVSEs belonging to this location.
     *
     * @return A list of EVSEs.
     */
    public List<EVSE> getEvses() {
        return Collections.unmodifiableList(evses);
    }
}