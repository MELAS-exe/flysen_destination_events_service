package com.spacehackers.flysen.flysen_destination_events_service.controllers;

import com.spacehackers.flysen.flysen_destination_events_service.dtos.ApiResponse;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.AirportServiceDTO;
import com.spacehackers.flysen.flysen_destination_events_service.models.AirportService;
import com.spacehackers.flysen.flysen_destination_events_service.services.AirportServiceService;
import com.spacehackers.flysen.flysen_destination_events_service.util.AirportServiceMapper;
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
@RequestMapping("/airport-services")
@RequiredArgsConstructor
@Tag(name = "Airport Services", description = "Airport services and facilities management APIs")
public class AirportServiceController {

    private final AirportServiceService airportServiceService;

    @PostMapping
    @Operation(summary = "Create a new airport service")
    public ResponseEntity<ApiResponse<AirportServiceDTO>> createAirportService(
            @Valid @RequestBody AirportServiceDTO serviceDTO) {
        log.info("Creating new airport service: {}", serviceDTO.getName());

        AirportService service = AirportServiceMapper.toModel(serviceDTO);
        String id = airportServiceService.createAirportService(service);

        AirportService created = airportServiceService.getAirportServiceById(id).orElse(service);
        AirportServiceDTO responseDTO = AirportServiceMapper.toDTO(created);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AirportServiceDTO>builder()
                        .success(true)
                        .message("Airport service created successfully")
                        .data(responseDTO)
                        .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport service by ID")
    public ResponseEntity<ApiResponse<AirportServiceDTO>> getAirportService(@PathVariable String id) {
        log.info("Fetching airport service with ID: {}", id);
        return airportServiceService.getAirportServiceById(id)
                .map(service -> ResponseEntity.ok(
                        ApiResponse.<AirportServiceDTO>builder()
                                .success(true)
                                .message("Airport service retrieved successfully")
                                .data(AirportServiceMapper.toDTO(service))
                                .build()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<AirportServiceDTO>builder()
                                .success(false)
                                .message("Airport service not found")
                                .build()));
    }

    @GetMapping
    @Operation(summary = "Get all airport services with pagination")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> getAllAirportServices(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String lastDocumentId) {
        log.info("Fetching airport services with limit: {}", limit);
        List<AirportService> services = airportServiceService.getAllAirportServices(limit, lastDocumentId);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Airport services retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/airport/{airportId}")
    @Operation(summary = "Get all services for a specific airport")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> getServicesByAirport(
            @PathVariable String airportId) {
        log.info("Fetching services for airport: {}", airportId);
        List<AirportService> services = airportServiceService.getServicesByAirport(airportId);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Services retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/airport-code/{airportCode}")
    @Operation(summary = "Get all services for an airport by airport code")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> getServicesByAirportCode(
            @PathVariable String airportCode) {
        log.info("Fetching services for airport code: {}", airportCode);
        List<AirportService> services = airportServiceService.getServicesByAirportCode(airportCode);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Services retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/airport/{airportId}/category/{category}")
    @Operation(summary = "Get services by category for a specific airport")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> getServicesByCategory(
            @PathVariable String airportId,
            @PathVariable String category) {
        log.info("Fetching services for airport {} with category: {}", airportId, category);
        AirportService.ServiceCategory serviceCategory = AirportService.ServiceCategory.valueOf(category);
        List<AirportService> services = airportServiceService.getServicesByCategory(airportId, serviceCategory);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Services retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/airport/{airportId}/top-rated")
    @Operation(summary = "Get top-rated services for a specific airport")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> getTopRatedServices(
            @PathVariable String airportId,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} rated services for airport: {}", limit, airportId);
        List<AirportService> services = airportServiceService.getTopRatedServices(airportId, limit);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Top-rated services retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/airport/{airportId}/terminal/{terminal}")
    @Operation(summary = "Get services by terminal")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> getServicesByTerminal(
            @PathVariable String airportId,
            @PathVariable String terminal) {
        log.info("Fetching services for airport {} terminal: {}", airportId, terminal);
        List<AirportService> services = airportServiceService.getServicesByTerminal(airportId, terminal);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Services retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/airport/{airportId}/search")
    @Operation(summary = "Search services at an airport")
    public ResponseEntity<ApiResponse<List<AirportServiceDTO>>> searchServices(
            @PathVariable String airportId,
            @RequestParam String query) {
        log.info("Searching services at airport {} with query: {}", airportId, query);
        List<AirportService> services = airportServiceService.searchServices(airportId, query);
        List<AirportServiceDTO> dtos = services.stream()
                .map(AirportServiceMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<AirportServiceDTO>>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(dtos)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airport service")
    public ResponseEntity<ApiResponse<Void>> updateAirportService(
            @PathVariable String id,
            @Valid @RequestBody AirportServiceDTO serviceDTO) {
        log.info("Updating airport service with ID: {}", id);
        AirportService service = AirportServiceMapper.toModel(serviceDTO);
        airportServiceService.updateAirportService(id, service);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Airport service updated successfully")
                        .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airport service")
    public ResponseEntity<ApiResponse<Void>> deleteAirportService(@PathVariable String id) {
        log.info("Deleting airport service with ID: {}", id);
        airportServiceService.deleteAirportService(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Airport service deleted successfully")
                        .build());
    }

    // Product management endpoints
    @PostMapping("/{serviceId}/products")
    @Operation(summary = "Add a product to an airport service")
    public ResponseEntity<ApiResponse<Void>> addProduct(
            @PathVariable String serviceId,
            @Valid @RequestBody AirportServiceDTO.AirportServiceProductDTO productDTO) {
        log.info("Adding product to service ID: {}", serviceId);
        AirportService.AirportServiceProduct product = AirportServiceMapper.toProductModel(productDTO);
        airportServiceService.addProduct(serviceId, product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Product added successfully")
                        .build());
    }

    @PutMapping("/{serviceId}/products/{productId}")
    @Operation(summary = "Update a product in an airport service")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable String serviceId,
            @PathVariable String productId,
            @Valid @RequestBody AirportServiceDTO.AirportServiceProductDTO productDTO) {
        log.info("Updating product {} in service ID: {}", productId, serviceId);
        AirportService.AirportServiceProduct product = AirportServiceMapper.toProductModel(productDTO);
        airportServiceService.updateProduct(serviceId, productId, product);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Product updated successfully")
                        .build());
    }

    @DeleteMapping("/{serviceId}/products/{productId}")
    @Operation(summary = "Remove a product from an airport service")
    public ResponseEntity<ApiResponse<Void>> removeProduct(
            @PathVariable String serviceId,
            @PathVariable String productId) {
        log.info("Removing product {} from service ID: {}", productId, serviceId);
        airportServiceService.removeProduct(serviceId, productId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Product removed successfully")
                        .build());
    }
}