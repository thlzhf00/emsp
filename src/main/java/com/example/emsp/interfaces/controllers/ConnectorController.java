package com.example.emsp.interfaces.controllers;

import com.example.emsp.application.ConnectorApplicationService;
import com.example.emsp.domain.location.Connector;
import com.example.emsp.interfaces.dtos.ConnectorRequestDto;
import com.example.emsp.interfaces.dtos.ConnectorResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Connector entities.
 * Exposes API endpoints for adding Connectors to EVSEs.
 */
@RestController
@RequestMapping("/api/v1") // Base path for Connector specific operations
@RequiredArgsConstructor
public class ConnectorController {

    private final ConnectorApplicationService connectorApplicationService;

    /**
     * Adds a new Connector to a specific EVSE.
     *
     * @param evseId      The OCPI-compliant EVSE ID string of the EVSE to which the connector will be added.
     * @param requestDto The request body containing connector details.
     * @return ResponseEntity with the created ConnectorResponseDto and HTTP 201 Created status.
     */
    @PostMapping("/evses/{evseId}/connectors")
    public ResponseEntity<ConnectorResponseDto> addConnectorToEVSE(
            @PathVariable String evseId,
            @Valid @RequestBody ConnectorRequestDto requestDto) {
        Connector connector = connectorApplicationService.addConnectorToEVSE(
                evseId,
                requestDto.getStandard(),
                requestDto.getPowerLevel(),
                requestDto.getVoltage()
        );
        return new ResponseEntity<>(ConnectorResponseDto.fromEntity(connector), HttpStatus.CREATED);
    }
}