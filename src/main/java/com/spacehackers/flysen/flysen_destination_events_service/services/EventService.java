package com.spacehackers.flysen.flysen_destination_events_service.services;

import com.google.cloud.Timestamp;
import com.spacehackers.flysen.flysen_destination_events_service.models.Event;
import com.spacehackers.flysen.flysen_destination_events_service.repositories.EventRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @CircuitBreaker(name = "firestore", fallbackMethod = "createEventFallback")
    public String createEvent(Event event) {
        try {
            // Set initial stats
            if (event.getStats() == null) {
                event.setStats(Event.EventStats.builder()
                        .totalBookings(0)
                        .totalViews(0)
                        .averageRating(0.0)
                        .totalReviews(0)
                        .expressOffersCount(0)
                        .build());
            }

            return eventRepository.create(event);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error creating event: {}", e.getMessage());
            throw new RuntimeException("Failed to create event", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getEventFallback")
    public Optional<Event> getEventById(String id) {
        try {
            return eventRepository.findById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching event: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch event", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getAllEventsFallback")
    public List<Event> getAllEvents(int limit, String lastDocumentId) {
        try {
            return eventRepository.findAll(limit, lastDocumentId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching events: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch events", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getEventsByDestinationFallback")
    public List<Event> getEventsByDestination(String destinationId) {
        try {
            return eventRepository.findByDestination(destinationId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching events by destination: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch events by destination", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getUpcomingEventsFallback")
    public List<Event> getUpcomingEvents(int days, int limit) {
        try {
            Timestamp startDate = Timestamp.now();
            // Add days to current timestamp
            long secondsToAdd = TimeUnit.DAYS.toSeconds(days);
            Timestamp endDate = Timestamp.ofTimeSecondsAndNanos(
                    startDate.getSeconds() + secondsToAdd,
                    startDate.getNanos()
            );
            return eventRepository.findUpcoming(startDate, endDate, limit);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching upcoming events: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch upcoming events", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getFeaturedEventsFallback")
    public List<Event> getFeaturedEvents(int limit) {
        try {
            return eventRepository.findFeatured(limit);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching featured events: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch featured events", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getEventsByTypeFallback")
    public List<Event> getEventsByType(Event.EventType type) {
        try {
            return eventRepository.findByType(type);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching events by type: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch events by type", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "updateEventFallback")
    public void updateEvent(String id, Event event) {
        try {
            eventRepository.update(id, event);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error updating event: {}", e.getMessage());
            throw new RuntimeException("Failed to update event", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "deleteEventFallback")
    public void deleteEvent(String id) {
        try {
            eventRepository.delete(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error deleting event: {}", e.getMessage());
            throw new RuntimeException("Failed to delete event", e);
        }
    }

    // Fallback methods
    private String createEventFallback(Event event, Exception e) {
        log.error("Circuit breaker fallback: Failed to create event", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private Optional<Event> getEventFallback(String id, Exception e) {
        log.error("Circuit breaker fallback: Failed to get event", e);
        return Optional.empty();
    }

    private List<Event> getAllEventsFallback(int limit, String lastDocumentId, Exception e) {
        log.error("Circuit breaker fallback: Failed to get all events", e);
        return List.of();
    }

    private List<Event> getEventsByDestinationFallback(String destinationId, Exception e) {
        log.error("Circuit breaker fallback: Failed to get events by destination", e);
        return List.of();
    }

    private List<Event> getUpcomingEventsFallback(int days, int limit, Exception e) {
        log.error("Circuit breaker fallback: Failed to get upcoming events", e);
        return List.of();
    }

    private List<Event> getFeaturedEventsFallback(int limit, Exception e) {
        log.error("Circuit breaker fallback: Failed to get featured events", e);
        return List.of();
    }

    private List<Event> getEventsByTypeFallback(Event.EventType type, Exception e) {
        log.error("Circuit breaker fallback: Failed to get events by type", e);
        return List.of();
    }

    private void updateEventFallback(String id, Event event, Exception e) {
        log.error("Circuit breaker fallback: Failed to update event", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private void deleteEventFallback(String id, Exception e) {
        log.error("Circuit breaker fallback: Failed to delete event", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
}