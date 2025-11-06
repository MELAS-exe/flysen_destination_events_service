package com.spacehackers.flysen.flysen_destination_events_service.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.Timestamp;
import com.spacehackers.flysen.flysen_destination_events_service.models.Destination;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @CircuitBreaker(name = "weather-api", fallbackMethod = "getWeatherInfoFallback")
    public Destination.WeatherInfo getWeatherInfo(Double latitude, Double longitude) {
        try {
            String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=metric",
                    weatherApiUrl, latitude, longitude, weatherApiKey);

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response != null) {
                return Destination.WeatherInfo.builder()
                        .temperature(response.path("main").path("temp").asDouble())
                        .humidity(response.path("main").path("humidity").asInt())
                        .condition(response.path("weather").get(0).path("main").asText())
                        .description(response.path("weather").get(0).path("description").asText())
                        .lastUpdated(Timestamp.now())  // Changed from LocalDateTime
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage());
        }

        return null;
    }

    private Destination.WeatherInfo getWeatherInfoFallback(Double latitude, Double longitude, Exception e) {
        log.warn("Weather API fallback triggered: {}", e.getMessage());
        return Destination.WeatherInfo.builder()
                .temperature(null)
                .humidity(null)
                .condition("Unavailable")
                .description("Weather information temporarily unavailable")
                .lastUpdated(Timestamp.now())  // Changed from LocalDateTime
                .build();
    }
}