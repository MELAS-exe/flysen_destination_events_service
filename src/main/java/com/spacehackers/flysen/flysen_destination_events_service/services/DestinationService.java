package com.spacehackers.flysen.flysen_destination_events_service.services;

//import com.sembene.destinations.model.Destination;
//import com.sembene.destinations.repository.DestinationRepository;
import com.spacehackers.flysen.flysen_destination_events_service.models.Destination;
import com.spacehackers.flysen.flysen_destination_events_service.repositories.DestinationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final WeatherService weatherService;
    private final PlacesService placesService;

    @CircuitBreaker(name = "firestore", fallbackMethod = "createDestinationFallback")
    public String createDestination(Destination destination) {
        try {
            // Enrich with weather data if coordinates are provided
            if (destination.getLatitude() != null && destination.getLongitude() != null) {
                try {
                    Destination.WeatherInfo weather = weatherService.getWeatherInfo(
                            destination.getLatitude(), 
                            destination.getLongitude()
                    );
                    destination.setCurrentWeather(weather);
                } catch (Exception e) {
                    log.warn("Failed to fetch weather for destination: {}", e.getMessage());
                }
            }

            return destinationRepository.create(destination);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error creating destination: {}", e.getMessage());
            throw new RuntimeException("Failed to create destination", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getDestinationFallback")
    public Optional<Destination> getDestinationById(String id) {
        try {
            Optional<Destination> destination = destinationRepository.findById(id);
            
            // Update weather if destination exists
            destination.ifPresent(dest -> {
                if (dest.getLatitude() != null && dest.getLongitude() != null) {
                    try {
                        Destination.WeatherInfo weather = weatherService.getWeatherInfo(
                                dest.getLatitude(), 
                                dest.getLongitude()
                        );
                        dest.setCurrentWeather(weather);
                    } catch (Exception e) {
                        log.warn("Failed to update weather for destination: {}", e.getMessage());
                    }
                }
            });
            
            return destination;
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching destination: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch destination", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getAllDestinationsFallback")
    public List<Destination> getAllDestinations(int limit, String lastDocumentId) {
        try {
            return destinationRepository.findAll(limit, lastDocumentId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching destinations: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch destinations", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getDestinationsByRegionFallback")
    public List<Destination> getDestinationsByRegion(String region) {
        try {
            return destinationRepository.findByRegion(region);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching destinations by region: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch destinations by region", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getPopularDestinationsFallback")
    public List<Destination> getPopularDestinations(int limit) {
        try {
            return destinationRepository.findPopular(limit);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching popular destinations: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch popular destinations", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "searchDestinationsFallback")
    public List<Destination> searchDestinations(String searchTerm) {
        try {
            return destinationRepository.search(searchTerm);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error searching destinations: {}", e.getMessage());
            throw new RuntimeException("Failed to search destinations", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "updateDestinationFallback")
    public void updateDestination(String id, Destination destination) {
        try {
            destinationRepository.update(id, destination);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error updating destination: {}", e.getMessage());
            throw new RuntimeException("Failed to update destination", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "deleteDestinationFallback")
    public void deleteDestination(String id) {
        try {
            destinationRepository.delete(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error deleting destination: {}", e.getMessage());
            throw new RuntimeException("Failed to delete destination", e);
        }
    }

    // Fallback methods
    private String createDestinationFallback(Destination destination, Exception e) {
        log.error("Circuit breaker fallback: Failed to create destination", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private Optional<Destination> getDestinationFallback(String id, Exception e) {
        log.error("Circuit breaker fallback: Failed to get destination", e);
        return Optional.empty();
    }

    private List<Destination> getAllDestinationsFallback(int limit, String lastDocumentId, Exception e) {
        log.error("Circuit breaker fallback: Failed to get all destinations", e);
        return List.of();
    }

    private List<Destination> getDestinationsByRegionFallback(String region, Exception e) {
        log.error("Circuit breaker fallback: Failed to get destinations by region", e);
        return List.of();
    }

    private List<Destination> getPopularDestinationsFallback(int limit, Exception e) {
        log.error("Circuit breaker fallback: Failed to get popular destinations", e);
        return List.of();
    }

    private List<Destination> searchDestinationsFallback(String searchTerm, Exception e) {
        log.error("Circuit breaker fallback: Failed to search destinations", e);
        return List.of();
    }

    private void updateDestinationFallback(String id, Destination destination, Exception e) {
        log.error("Circuit breaker fallback: Failed to update destination", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private void deleteDestinationFallback(String id, Exception e) {
        log.error("Circuit breaker fallback: Failed to delete destination", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
}