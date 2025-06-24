package com.example.emsp.domain.evse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EVSE entities.
 * Extends JpaRepository to provide standard CRUD operations.
 * Allows finding EVSEs by their unique EVSEId value.
 */
@Repository
public interface EVSERepository extends JpaRepository<EVSE, Long> {

    /**
     * Finds an EVSE by its EVSE ID string value.
     *
     * @param text The string value of the EVSE ID.
     * @return An Optional containing the EVSE if found, or empty otherwise.
     */
    Optional<EVSE> findByEvseIdText(String text);
}