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
public class EventDTO {
    
    private String id;
    private String name;
    private String type;  // EventType as String
    private String destinationId;
    private String destinationName;
    private String date;  // ISO 8601 format
    private String endDate;  // ISO 8601 format
    private String venue;
    private String description;
    private List<String> images;
    private Double ticketPrice;
    private Integer capacity;
    private Integer remainingCapacity;
    private boolean featured;
    
    // Location
    private Double latitude;
    private Double longitude;
    private String address;
    
    // Organizer information
    private OrganizerInfoDTO organizer;
    
    // Statistics
    private EventStatsDTO stats;
    
    // Metadata
    private String createdAt;  // ISO 8601 format
    private String updatedAt;  // ISO 8601 format
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;
    private String status;  // EventStatus as String
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizerInfoDTO {
        private String name;
        private String email;
        private String phone;
        private String website;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventStatsDTO {
        private Integer totalBookings;
        private Integer totalViews;
        private Double averageRating;
        private Integer totalReviews;
        private Integer expressOffersCount;
    }
}