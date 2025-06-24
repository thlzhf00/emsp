package com.example.emsp.domain.location;

import com.example.emsp.domain.events.ConnectorAddedEvent;
import com.example.emsp.domain.evse.EVSE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a physical charging port on an EVSE.
 * It belongs to exactly one EVSE.
 * This entity is part of the EVSE aggregate.
 */
@Entity
@Table(name = "connectors")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Protected constructor for JPA
@EntityListeners(AuditingEntityListener.class) // For automatic 'lastUpdated'
public class Connector extends AbstractAggregateRoot<Connector> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Standard cannot be blank")
    private String standard; // e.g., "IEC_62196_T2", "CHADEMO", "CCS_TYPE_2"

    @NotNull(message = "Power level cannot be null")
    @Positive(message = "Power level must be positive")
    private Double powerLevel; // in kW

    @NotNull(message = "Voltage cannot be null")
    @Positive(message = "Voltage must be positive")
    private Double voltage; // in Volts

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evse_id", nullable = false)
    private EVSE evse; // Foreign key to EVSE

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated; // Automatically managed by JPA Auditing

    /**
     * Constructor for creating a new Connector.
     *
     * @param standard   The technical standard of the connector.
     * @param powerLevel The power level (in kW) of the connector.
     * @param voltage    The voltage (in Volts) of the connector.
     * @param evse       The EVSE to which this connector belongs.
     */
    public Connector(String standard, Double powerLevel, Double voltage, EVSE evse) {
        this.standard = standard;
        this.powerLevel = powerLevel;
        this.voltage = voltage;
        this.evse = evse;
        // lastUpdated will be set by @EnableJpaAuditing on persist

        // Register the event. This will be published after transaction commit.
        // Note: this.id might be null here if not yet persisted, but will be populated by
        // JPA before event publishing.
        registerEvent(new ConnectorAddedEvent(this.id,
                this.evse.getId(),
                this.evse.getEvseId() == null ? "" : this.evse.getEvseId().getText(),
                this.standard,
                this.powerLevel,
                this.voltage));
    }

    // Methods for domain behavior if needed
}
