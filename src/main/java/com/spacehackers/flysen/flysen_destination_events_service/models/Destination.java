package com.spacehackers.flysen.flysen_destination_events_service.models;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Destination {

    private String id;
    private String name;
    private String region;
    private String nearestAirportId;
    private String nearestAirportCode;
    private String description;
    private List<String> highlights;
    private List<String> images;
    private List<String> videos;
    private String virtualTourUrl;
    private String bestSeason;
    private Integer averageStayDuration;
    private Double popularityScore;
    private Double latitude;
    private Double longitude;

    // Weather information
    private WeatherInfo currentWeather;

    // Statistics
    private DestinationStats stats;

    // Metadata - USE FIRESTORE TIMESTAMP INSTEAD OF LOCALDATETIME
    private Timestamp createdAt;  // Changed from LocalDateTime
    private Timestamp updatedAt;  // Changed from LocalDateTime
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInfo {
        private Double temperature;
        private String condition;
        private Integer humidity;
        private String description;
        private Timestamp lastUpdated;  // Changed from LocalDateTime
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinationStats {
        private Integer totalAttractions;
        private Integer totalAccommodations;
        private Integer totalActivities;
        private Integer totalEvents;
        private Double averageRating;
        private Integer totalReviews;
        private Integer monthlyVisitors;
    }
}