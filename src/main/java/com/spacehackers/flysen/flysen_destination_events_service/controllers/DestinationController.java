package com.spacehackers.flysen.flysen_destination_events_service.controllers;

import com.spacehackers.flysen.flysen_destination_events_service.dtos.ApiResponse;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.DestinationDTO;
import com.spacehackers.flysen.flysen_destination_events_service.models.Destination;
import com.spacehackers.flysen.flysen_destination_events_service.services.DestinationService;
import com.spacehackers.flysen.flysen_destination_events_service.util.ModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/destinations")
@RequiredArgsConstructor
@Tag(name = "Destinations", description = "Destination management APIs")
public class DestinationController {

    private final DestinationService destinationService;

    @PostMapping
    @Operation(summary = "Create a new destination")
    public ResponseEntity<ApiResponse<DestinationDTO>> createDestination(@Valid @RequestBody DestinationDTO destinationDTO) {
        log.info("Creating new destination: {}", destinationDTO.getName());

        // Convert DTO to Model
        Destination destination = ModelMapper.toModel(destinationDTO);

        // Create destination
        String id = destinationService.createDestination(destination);

        // Get the created destination and convert back to DTO
        Destination created = destinationService.getDestinationById(id).orElse(destination);
        DestinationDTO responseDTO = ModelMapper.toDTO(created);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DestinationDTO>builder()
                        .success(true)
                        .message("Destination created successfully")
                        .data(responseDTO)
                        .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get destination by ID")
    public ResponseEntity<ApiResponse<DestinationDTO>> getDestination(@PathVariable String id) {
        log.info("Fetching destination with ID: {}", id);
        return destinationService.getDestinationById(id)
                .map(destination -> ResponseEntity.ok(
                        ApiResponse.<DestinationDTO>builder()
                                .success(true)
                                .message("Destination retrieved successfully")
                                .data(ModelMapper.toDTO(destination))
                                .build()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<DestinationDTO>builder()
                                .success(false)
                                .message("Destination not found")
                                .build()));
    }

    @GetMapping
    @Operation(summary = "Get all destinations with pagination")
    public ResponseEntity<ApiResponse<List<DestinationDTO>>> getAllDestinations(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String lastDocumentId) {
        log.info("Fetching destinations with limit: {}", limit);
        List<Destination> destinations = destinationService.getAllDestinations(limit, lastDocumentId);
        List<DestinationDTO> dtos = destinations.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<DestinationDTO>>builder()
                        .success(true)
                        .message("Destinations retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/region/{region}")
    @Operation(summary = "Get destinations by region")
    public ResponseEntity<ApiResponse<List<DestinationDTO>>> getDestinationsByRegion(@PathVariable String region) {
        log.info("Fetching destinations for region: {}", region);
        List<Destination> destinations = destinationService.getDestinationsByRegion(region);
        List<DestinationDTO> dtos = destinations.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<DestinationDTO>>builder()
                        .success(true)
                        .message("Destinations retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular destinations")
    public ResponseEntity<ApiResponse<List<DestinationDTO>>> getPopularDestinations(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching popular destinations with limit: {}", limit);
        List<Destination> destinations = destinationService.getPopularDestinations(limit);
        List<DestinationDTO> dtos = destinations.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<DestinationDTO>>builder()
                        .success(true)
                        .message("Popular destinations retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search destinations")
    public ResponseEntity<ApiResponse<List<DestinationDTO>>> searchDestinations(
            @RequestParam String query) {
        log.info("Searching destinations with query: {}", query);
        List<Destination> destinations = destinationService.searchDestinations(query);
        List<DestinationDTO> dtos = destinations.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<DestinationDTO>>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(dtos)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update destination")
    public ResponseEntity<ApiResponse<Void>> updateDestination(
            @PathVariable String id,
            @Valid @RequestBody DestinationDTO destinationDTO) {
        log.info("Updating destination with ID: {}", id);
        Destination destination = ModelMapper.toModel(destinationDTO);
        destinationService.updateDestination(id, destination);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Destination updated successfully")
                        .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete destination")
    public ResponseEntity<ApiResponse<Void>> deleteDestination(@PathVariable String id) {
        log.info("Deleting destination with ID: {}", id);
        destinationService.deleteDestination(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Destination deleted successfully")
                        .build());
    }
}