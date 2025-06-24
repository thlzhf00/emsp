package com.example.emsp.domain.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository interface for Location entities.
 * Extends JpaRepository to provide standard CRUD operations.
 * Allows querying locations based on lastUpdated timestamp with pagination.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Finds all Locations that were last updated after a given timestamp.
     * The results are paginated.
     *
     * @param lastUpdated Timestamp to filter locations by.
     * @param pageable    Pagination information.
     * @return A Page of Location entities.
     */
    Page<Location> findByLastUpdatedAfter(LocalDateTime lastUpdated, Pageable pageable);
}