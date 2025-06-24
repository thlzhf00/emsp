package com.example.emsp.interfaces.controllers;

import com.example.emsp.application.EVSEApplicationService;
import com.example.emsp.domain.evse.EVSE;
import com.example.emsp.interfaces.dtos.EVSECreateRequestDto;
import com.example.emsp.interfaces.dtos.EVSEResponseDto;
import com.example.emsp.interfaces.dtos.EVSEStatusUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing EVSE entities.
 * Exposes API endpoints for adding EVSEs to locations and changing EVSE status.
 */
@RestController
@RequestMapping("/api/v1") // Base path for EVSE specific operations
@RequiredArgsConstructor
public class EVSEController {

    private final EVSEApplicationService evseApplicationService;

    /**
     * Adds a new EVSE to a specific Location.
     *
     * @param locationId  The ID of the location to which the EVSE will be added.
     * @param requestDto The request body containing EVSE details.
     * @return ResponseEntity with the created EVSEResponseDto and HTTP 201 Created status.
     */
    @PostMapping("/locations/{locationId}/evses")
    public ResponseEntity<EVSEResponseDto> addEVSEToLocation(
            @PathVariable Long locationId,
            @Valid @RequestBody EVSECreateRequestDto requestDto) {
        EVSE evse = evseApplicationService.addEVSEToLocation(
                locationId,
                requestDto.getEvseId() // EVSEId validation is handled in EVSEId constructor
        );
        return new ResponseEntity<>(EVSEResponseDto.fromEntity(evse), HttpStatus.CREATED);
    }

    /**
     * Changes the status of an existing EVSE.
     * This operation enforces the defined state transition rules.
     *
     * @param evseId      The OCPI-compliant EVSE ID string of the EVSE to update.
     * @param requestDto The request body containing the new status.
     * @return ResponseEntity with the updated EVSEResponseDto and HTTP 200 OK status.
     */
    @PatchMapping("/evses/{evseId}/status")
    public ResponseEntity<EVSEResponseDto> changeEVSEStatus(
            @PathVariable String evseId,
            @Valid @RequestBody EVSEStatusUpdateRequestDto requestDto) {
        EVSE evse = evseApplicationService.changeEVSEStatus(
                evseId,
                requestDto.getNewStatus()
        );
        return new ResponseEntity<>(EVSEResponseDto.fromEntity(evse), HttpStatus.OK);
    }
}