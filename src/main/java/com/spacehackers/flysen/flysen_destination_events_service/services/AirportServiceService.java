package com.spacehackers.flysen.flysen_destination_events_service.services;

import com.spacehackers.flysen.flysen_destination_events_service.models.AirportService;
import com.spacehackers.flysen.flysen_destination_events_service.repositories.AirportServiceRepository;
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
public class AirportServiceService {

    private final AirportServiceRepository airportServiceRepository;

    @CircuitBreaker(name = "firestore", fallbackMethod = "createAirportServiceFallback")
    public String createAirportService(AirportService airportService) {
        try {
            // Initialize default values if needed
            if (airportService.getRating() == null) {
                airportService.setRating(0.0);
            }
            if (airportService.getReviewsCount() == null) {
                airportService.setReviewsCount(0);
            }
            if (airportService.getScore() == null) {
                airportService.setScore(0.0);
            }

            return airportServiceRepository.create(airportService);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error creating airport service: {}", e.getMessage());
            throw new RuntimeException("Failed to create airport service", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getAirportServiceFallback")
    public Optional<AirportService> getAirportServiceById(String id) {
        try {
            return airportServiceRepository.findById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching airport service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch airport service", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getAllAirportServicesFallback")
    public List<AirportService> getAllAirportServices(int limit, String lastDocumentId) {
        try {
            return airportServiceRepository.findAll(limit, lastDocumentId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching airport services: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch airport services", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getServicesByAirportFallback")
    public List<AirportService> getServicesByAirport(String airportId) {
        try {
            return airportServiceRepository.findByAirport(airportId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching services by airport: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch services by airport", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getServicesByAirportCodeFallback")
    public List<AirportService> getServicesByAirportCode(String airportCode) {
        try {
            return airportServiceRepository.findByAirportCode(airportCode);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching services by airport code: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch services by airport code", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getServicesByCategoryFallback")
    public List<AirportService> getServicesByCategory(String airportId, AirportService.ServiceCategory category) {
        try {
            return airportServiceRepository.findByCategory(airportId, category);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching services by category: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch services by category", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getTopRatedServicesFallback")
    public List<AirportService> getTopRatedServices(String airportId, int limit) {
        try {
            return airportServiceRepository.findTopRated(airportId, limit);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching top rated services: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch top rated services", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "getServicesByTerminalFallback")
    public List<AirportService> getServicesByTerminal(String airportId, String terminal) {
        try {
            return airportServiceRepository.findByTerminal(airportId, terminal);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching services by terminal: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch services by terminal", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "searchServicesFallback")
    public List<AirportService> searchServices(String airportId, String searchTerm) {
        try {
            return airportServiceRepository.search(airportId, searchTerm);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error searching services: {}", e.getMessage());
            throw new RuntimeException("Failed to search services", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "updateAirportServiceFallback")
    public void updateAirportService(String id, AirportService airportService) {
        try {
            airportServiceRepository.update(id, airportService);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error updating airport service: {}", e.getMessage());
            throw new RuntimeException("Failed to update airport service", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "deleteAirportServiceFallback")
    public void deleteAirportService(String id) {
        try {
            airportServiceRepository.delete(id);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error deleting airport service: {}", e.getMessage());
            throw new RuntimeException("Failed to delete airport service", e);
        }
    }

    // Product management methods
    @CircuitBreaker(name = "firestore", fallbackMethod = "addProductFallback")
    public void addProduct(String serviceId, AirportService.AirportServiceProduct product) {
        try {
            airportServiceRepository.addProduct(serviceId, product);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error adding product: {}", e.getMessage());
            throw new RuntimeException("Failed to add product", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "updateProductFallback")
    public void updateProduct(String serviceId, String productId, 
                            AirportService.AirportServiceProduct product) {
        try {
            airportServiceRepository.updateProduct(serviceId, productId, product);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error updating product: {}", e.getMessage());
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @CircuitBreaker(name = "firestore", fallbackMethod = "removeProductFallback")
    public void removeProduct(String serviceId, String productId) {
        try {
            airportServiceRepository.removeProduct(serviceId, productId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error removing product: {}", e.getMessage());
            throw new RuntimeException("Failed to remove product", e);
        }
    }

    // Fallback methods
    private String createAirportServiceFallback(AirportService airportService, Exception e) {
        log.error("Circuit breaker fallback: Failed to create airport service", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private Optional<AirportService> getAirportServiceFallback(String id, Exception e) {
        log.error("Circuit breaker fallback: Failed to get airport service", e);
        return Optional.empty();
    }

    private List<AirportService> getAllAirportServicesFallback(int limit, String lastDocumentId, Exception e) {
        log.error("Circuit breaker fallback: Failed to get all airport services", e);
        return List.of();
    }

    private List<AirportService> getServicesByAirportFallback(String airportId, Exception e) {
        log.error("Circuit breaker fallback: Failed to get services by airport", e);
        return List.of();
    }

    private List<AirportService> getServicesByAirportCodeFallback(String airportCode, Exception e) {
        log.error("Circuit breaker fallback: Failed to get services by airport code", e);
        return List.of();
    }

    private List<AirportService> getServicesByCategoryFallback(String airportId, 
            AirportService.ServiceCategory category, Exception e) {
        log.error("Circuit breaker fallback: Failed to get services by category", e);
        return List.of();
    }

    private List<AirportService> getTopRatedServicesFallback(String airportId, int limit, Exception e) {
        log.error("Circuit breaker fallback: Failed to get top rated services", e);
        return List.of();
    }

    private List<AirportService> getServicesByTerminalFallback(String airportId, String terminal, Exception e) {
        log.error("Circuit breaker fallback: Failed to get services by terminal", e);
        return List.of();
    }

    private List<AirportService> searchServicesFallback(String airportId, String searchTerm, Exception e) {
        log.error("Circuit breaker fallback: Failed to search services", e);
        return List.of();
    }

    private void updateAirportServiceFallback(String id, AirportService airportService, Exception e) {
        log.error("Circuit breaker fallback: Failed to update airport service", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private void deleteAirportServiceFallback(String id, Exception e) {
        log.error("Circuit breaker fallback: Failed to delete airport service", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private void addProductFallback(String serviceId, AirportService.AirportServiceProduct product, Exception e) {
        log.error("Circuit breaker fallback: Failed to add product", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private void updateProductFallback(String serviceId, String productId, 
                                      AirportService.AirportServiceProduct product, Exception e) {
        log.error("Circuit breaker fallback: Failed to update product", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private void removeProductFallback(String serviceId, String productId, Exception e) {
        log.error("Circuit breaker fallback: Failed to remove product", e);
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
}