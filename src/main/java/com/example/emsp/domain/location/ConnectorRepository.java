package com.example.emsp.domain.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Connector entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface ConnectorRepository extends JpaRepository<Connector, Long> {
    // No custom methods needed beyond what JpaRepository provides for now.
}