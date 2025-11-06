package com.spacehackers.flysen.flysen_destination_events_service.models;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TouristAttraction {

    private String id;
    private String destinationId;
    private String name;
    private AttractionType type;
    private String description;
    private List<String> images;
    private Double entranceFee;
    private Map<String, OpeningHours> openingHours;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double rating;
    private Integer reviewsCount;

    // Additional info
    private List<String> amenities;
    private String website;
    private String phoneNumber;
    private Integer estimatedDuration; // in minutes
    private String accessibilityInfo;

    // Categories and tags
    private List<String> tags;
    private boolean wheelchairAccessible;
    private boolean childFriendly;

    // Metadata
    private Timestamp createdAt;  // Changed from LocalDateTime
    private Timestamp updatedAt;  // Changed from LocalDateTime
    private boolean active;

    public enum AttractionType {
        MONUMENT,
        MUSEUM,
        PARK,
        BEACH,
        HISTORICAL_SITE,
        RELIGIOUS_SITE,
        ENTERTAINMENT,
        SHOPPING,
        NATURE,
        WILDLIFE,
        ARCHITECTURE,
        CULTURAL_CENTER,
        OTHER
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningHours {
        private String openTime;   // Store as String (e.g., "09:00")
        private String closeTime;  // Store as String (e.g., "17:00")
        private boolean closed;
    }
}