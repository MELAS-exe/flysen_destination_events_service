package com.spacehackers.flysen.flysen_destination_events_service.controllers;

import com.spacehackers.flysen.flysen_destination_events_service.dtos.ApiResponse;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.EventDTO;
import com.spacehackers.flysen.flysen_destination_events_service.models.Event;
import com.spacehackers.flysen.flysen_destination_events_service.services.EventService;
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
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management APIs")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create a new event")
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        log.info("Creating new event: {}", eventDTO.getName());

        // Convert DTO to Model
        Event event = ModelMapper.toModel(eventDTO);

        // Create event
        String id = eventService.createEvent(event);

        // Get the created event and convert back to DTO
        Event created = eventService.getEventById(id).orElse(event);
        EventDTO responseDTO = ModelMapper.toDTO(created);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EventDTO>builder()
                        .success(true)
                        .message("Event created successfully")
                        .data(responseDTO)
                        .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<ApiResponse<EventDTO>> getEvent(@PathVariable String id) {
        log.info("Fetching event with ID: {}", id);
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(
                        ApiResponse.<EventDTO>builder()
                                .success(true)
                                .message("Event retrieved successfully")
                                .data(ModelMapper.toDTO(event))
                                .build()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<EventDTO>builder()
                                .success(false)
                                .message("Event not found")
                                .build()));
    }

    @GetMapping
    @Operation(summary = "Get all events with pagination")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String lastDocumentId) {
        log.info("Fetching events with limit: {}", limit);
        List<Event> events = eventService.getAllEvents(limit, lastDocumentId);
        List<EventDTO> dtos = events.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<EventDTO>>builder()
                        .success(true)
                        .message("Events retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/destination/{destinationId}")
    @Operation(summary = "Get events by destination")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByDestination(@PathVariable String destinationId) {
        log.info("Fetching events for destination: {}", destinationId);
        List<Event> events = eventService.getEventsByDestination(destinationId);
        List<EventDTO> dtos = events.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<EventDTO>>builder()
                        .success(true)
                        .message("Events retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getUpcomingEvents(
            @RequestParam(defaultValue = "90") int days,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Fetching upcoming events for next {} days", days);
        List<Event> events = eventService.getUpcomingEvents(days, limit);
        List<EventDTO> dtos = events.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<EventDTO>>builder()
                        .success(true)
                        .message("Upcoming events retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured events")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getFeaturedEvents(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching featured events with limit: {}", limit);
        List<Event> events = eventService.getFeaturedEvents(limit);
        List<EventDTO> dtos = events.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<EventDTO>>builder()
                        .success(true)
                        .message("Featured events retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get events by type")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByType(@PathVariable String type) {
        log.info("Fetching events of type: {}", type);
        Event.EventType eventType = Event.EventType.valueOf(type);
        List<Event> events = eventService.getEventsByType(eventType);
        List<EventDTO> dtos = events.stream()
                .map(ModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<EventDTO>>builder()
                        .success(true)
                        .message("Events retrieved successfully")
                        .data(dtos)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event")
    public ResponseEntity<ApiResponse<Void>> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody EventDTO eventDTO) {
        log.info("Updating event with ID: {}", id);
        Event event = ModelMapper.toModel(eventDTO);
        eventService.updateEvent(id, event);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Event updated successfully")
                        .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String id) {
        log.info("Deleting event with ID: {}", id);
        eventService.deleteEvent(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Event deleted successfully")
                        .build());
    }
}