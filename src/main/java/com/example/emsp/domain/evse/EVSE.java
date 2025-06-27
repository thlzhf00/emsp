package com.example.emsp.domain.evse;

import com.example.emsp.domain.events.EVSEAddedEvent;
import com.example.emsp.domain.events.EVSEStatusChangedEvent;
import com.example.emsp.domain.location.Connector;
import com.example.emsp.domain.location.Location;
import com.example.emsp.infrastructure.exception.InvalidEVSEStatusTransitionException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an Electric Vehicle Supply Equipment (EVSE).
 * Each EVSE has a unique identifier (EVSEId) and an operational status.
 * It belongs to exactly one Location and contains multiple Connectors.
 * This entity is part of the Location aggregate.
 */
@Entity
@Table(name = "evses")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protected constructor for JPA
@EntityListeners(AuditingEntityListener.class) // For automatic 'lastUpdated'
public class EVSE extends AbstractAggregateRoot<EVSE> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Valid // Validates the embedded EVSEId value object
    @NotNull(message = "EVSE ID cannot be null")
    @Column(name = "evse_id_value", unique = true) // Map the EVSEId value to a column and ensure uniqueness
    private EVSEId evseId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "EVSE status cannot be null")
    private EVSEStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; // Foreign key to Location

    @OneToMany(mappedBy = "evse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Connector> connectors = new ArrayList<>();

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated; // Automatically managed by JPA Auditing

    /**
     * Constructor for creating a new EVSE.
     * Initial status is always AVAILABLE.
     *
     * @param evseId   The unique identifier for the EVSE.
     * @param location The Location to which this EVSE belongs.
     */
    public EVSE(EVSEId evseId, Location location) {
        this.evseId = evseId;
        this.location = location;
        this.status = EVSEStatus.AVAILABLE; // Initial state is AVAILABLE
        this.lastUpdated = LocalDateTime.now();

        // Register the event. This will be published after transaction commit.
        // Note: this.id might be null here if not yet persisted, but will be populated by JPA before event publishing.
        registerEvent(new EVSEAddedEvent(this.id, this.evseId.getText(), this.location.getId(), this.status));
    }

    /**
     * Changes the status of the EVSE, enforcing valid transition rules.
     *
     * @param newStatus The desired new status.
     * @throws InvalidEVSEStatusTransitionException if the status transition is not allowed.
     */
    public void changeStatus(EVSEStatus newStatus) {
        if (!EVSEStatus.isValidTransition(this.status, newStatus)) {
            throw new InvalidEVSEStatusTransitionException(
                    String.format("Invalid EVSE status transition from %s to %s for EVSE ID %s",
                            this.status, newStatus, this.evseId.getText()));
        }
        EVSEStatus oldStatus = this.status;
        this.status = newStatus;
        this.lastUpdated = LocalDateTime.now();

        // Register the event. This will be published after transaction commit.
        registerEvent(new EVSEStatusChangedEvent(this.id, this.evseId.getText(), oldStatus, newStatus));
    }

    /**
     * Adds a new Connector to this EVSE.
     *
     * @param connector The Connector to add.
     */
    public void addConnector(Connector connector) {
        if (connector != null) {
            this.connectors.add(connector);
            connector.setEvse(this); // Set bidirectional relationship
        }
    }

    /**
     * Removes a Connector from this EVSE.
     *
     * @param connector The Connector to remove.
     */
    public void removeConnector(Connector connector) {
        if (connector != null) {
            this.connectors.remove(connector);
            connector.setEvse(null); // Remove bidirectional relationship
        }
    }
}
