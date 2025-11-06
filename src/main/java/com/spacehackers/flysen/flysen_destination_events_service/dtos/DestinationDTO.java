package com.spacehackers.flysen.flysen_destination_events_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDTO {
    
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
    private WeatherInfoDTO currentWeather;
    
    // Statistics
    private DestinationStatsDTO stats;
    
    // Metadata - using String for dates
    private String createdAt;  // ISO 8601 format
    private String updatedAt;  // ISO 8601 format
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInfoDTO {
        private Double temperature;
        private String condition;
        private Integer humidity;
        private String description;
        private String lastUpdated;  // ISO 8601 format
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinationStatsDTO {
        private Integer totalAttractions;
        private Integer totalAccommodations;
        private Integer totalActivities;
        private Integer totalEvents;
        private Double averageRating;
        private Integer totalReviews;
        private Integer monthlyVisitors;
    }
}