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
public class Event {

    private String id;
    private String name;
    private EventType type;
    private String destinationId;
    private String destinationName;
    private Timestamp date;  // Changed from LocalDateTime
    private Timestamp endDate;  // Changed from LocalDateTime
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
    private OrganizerInfo organizer;

    // Statistics
    private EventStats stats;

    // Metadata
    private Timestamp createdAt;  // Changed from LocalDateTime
    private Timestamp updatedAt;  // Changed from LocalDateTime
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;
    private EventStatus status;

    public enum EventType {
        FESTIVAL,
        CONCERT,
        CONFERENCE,
        SPORTS,
        EXHIBITION,
        CULTURAL,
        RELIGIOUS,
        FOOD_WINE,
        NIGHTLIFE,
        OTHER
    }

    public enum EventStatus {
        SCHEDULED,
        ONGOING,
        COMPLETED,
        CANCELLED,
        POSTPONED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizerInfo {
        private String name;
        private String email;
        private String phone;
        private String website;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventStats {
        private Integer totalBookings;
        private Integer totalViews;
        private Double averageRating;
        private Integer totalReviews;
        private Integer expressOffersCount;
    }
}