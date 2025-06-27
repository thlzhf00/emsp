package com.example.emsp.interfaces.controllers;

import com.example.emsp.application.LocationApplicationService;
import com.example.emsp.domain.common.BusinessHours;
import com.example.emsp.domain.common.Coordinates;
import com.example.emsp.domain.location.Location;
import com.example.emsp.interfaces.dtos.LocationRequestDto;
import com.example.emsp.interfaces.dtos.LocationResponseDto;
import com.example.emsp.interfaces.dtos.LocationUpdateRequestDto;
import com.example.emsp.interfaces.dtos.LocationWithEVSEsResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for managing Location entities.
 * Exposes API endpoints for creating, updating, and querying locations.
 */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationApplicationService locationApplicationService;

    /**
     * Creates a new charging Location.
     *
     * @param requestDto The request body containing location details.
     * @return ResponseEntity with the created LocationResponseDto and HTTP 201 Created status.
     */
    @PostMapping
    public ResponseEntity<LocationResponseDto> createLocation(@Valid @RequestBody LocationRequestDto requestDto) {
        Location location = locationApplicationService.createLocation(
                requestDto.getName(),
                requestDto.getAddress(),
                new Coordinates(requestDto.getCoordinates().getLatitude(), requestDto.getCoordinates().getLongitude()),
                new BusinessHours(requestDto.getBusinessHours().getOpensAt(), requestDto.getBusinessHours().getClosesAt())
        );
        return new ResponseEntity<>(LocationResponseDto.fromEntity(location), HttpStatus.CREATED);
    }

    /**
     * Updates an existing charging Location.
     *
     * @param locationId The ID of the location to update.
     * @param requestDto The request body containing updated location details.
     * @return ResponseEntity with the updated LocationResponseDto and HTTP 200 OK status.
     */
    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponseDto> updateLocation(
            @PathVariable Long locationId,
            @Valid @RequestBody LocationUpdateRequestDto requestDto) {
        Location location = locationApplicationService.updateLocation(
                locationId,
                requestDto.getName(),
                requestDto.getAddress(),
                new Coordinates(requestDto.getCoordinates().getLatitude(), requestDto.getCoordinates().getLongitude()),
                new BusinessHours(requestDto.getBusinessHours().getOpensAt(), requestDto.getBusinessHours().getClosesAt())
        );
        return new ResponseEntity<>(LocationResponseDto.fromEntity(location), HttpStatus.OK);
    }

    /**
     * Queries Locations and their EVSEs by "last_updated" timestamp with pagination.
     *
     * @param lastUpdated Timestamp to filter locations by. Format: "yyyy-MM-dd'T'HH:mm:ss" (ISO 8601).
     * @param page        Page number (0-indexed, default 0).
     * @param size        Page size (default 10).
     * @return ResponseEntity with a Page of LocationWithEVSEsResponseDto and HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<Page<LocationWithEVSEsResponseDto>> queryLocations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastUpdated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // If lastUpdated is not provided, default to a very old date to fetch all.
        // In a real system, we might want to require this parameter or apply a different default logic.
        LocalDateTime filterTimestamp = (lastUpdated != null) ? lastUpdated : LocalDateTime.MIN;

        Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdated").ascending());
        Page<Location> locationsPage = locationApplicationService.queryLocationsByLastUpdated(filterTimestamp, pageable);

        Page<LocationWithEVSEsResponseDto> responsePage = locationsPage
                .map(LocationWithEVSEsResponseDto::fromEntity);

        return new ResponseEntity<>(responsePage, HttpStatus.OK);
    }

    /**
     * Retrieves a single Location by its ID, including its EVSEs and Connectors.
     *
     * @param locationId The ID of the location to retrieve.
     * @return ResponseEntity with the LocationWithEVSEsResponseDto and HTTP 200 OK status.
     */
    @GetMapping("/{locationId}")
    public ResponseEntity<LocationWithEVSEsResponseDto> getLocationById(@PathVariable Long locationId) {
        Location location = locationApplicationService.findLocationById(locationId);
        return new ResponseEntity<>(LocationWithEVSEsResponseDto.fromEntity(location), HttpStatus.OK);
    }
}
