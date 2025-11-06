package com.spacehackers.flysen.flysen_destination_events_service.services;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlacesService {

    private final GeoApiContext geoApiContext;

    @CircuitBreaker(name = "places-api", fallbackMethod = "searchNearbyPlacesFallback")
    public List<PlacesSearchResult> searchNearbyPlaces(Double latitude, Double longitude, int radius, String type) {
        try {
            com.google.maps.model.LatLng location = new com.google.maps.model.LatLng(latitude, longitude);
            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(geoApiContext, location)
                    .radius(radius)
                    .type(com.google.maps.model.PlaceType.valueOf(type.toUpperCase()))
                    .await();

            return Arrays.asList(response.results);
        } catch (Exception e) {
            log.error("Error searching nearby places: {}", e.getMessage());
            return List.of();
        }
    }

    @CircuitBreaker(name = "places-api", fallbackMethod = "getPlaceDetailsFallback")
    public PlaceDetails getPlaceDetails(String placeId) {
        try {
            return PlacesApi.placeDetails(geoApiContext, placeId).await();
        } catch (Exception e) {
            log.error("Error fetching place details: {}", e.getMessage());
            return null;
        }
    }

    private List<PlacesSearchResult> searchNearbyPlacesFallback(
            Double latitude, Double longitude, int radius, String type, Exception e) {
        log.warn("Places API fallback triggered for nearby search: {}", e.getMessage());
        return List.of();
    }

    private PlaceDetails getPlaceDetailsFallback(String placeId, Exception e) {
        log.warn("Places API fallback triggered for place details: {}", e.getMessage());
        return null;
    }
}