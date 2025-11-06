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
public class Activity {

    private String id;
    private String name;
    private String destinationId;
    private ActivityType type;
    private String description;
    private Integer duration; // in minutes
    private Double price;
    private List<String> images;
    private boolean bookingRequired;

    // Location
    private Double latitude;
    private Double longitude;
    private String meetingPoint;

    // Requirements
    private String difficulty;
    private Integer minAge;
    private Integer maxGroupSize;
    private List<String> requiredEquipment;
    private List<String> includedItems;

    // Availability
    private List<String> availableDays;
    private String startTime;  // Store as String (e.g., "09:00")
    private String endTime;    // Store as String (e.g., "17:00")

    // Rating
    private Double rating;
    private Integer reviewsCount;

    // Metadata
    private Timestamp createdAt;  // Changed from LocalDateTime
    private Timestamp updatedAt;  // Changed from LocalDateTime
    private boolean active;

    public enum ActivityType {
        ADVENTURE,
        CULTURAL,
        FOOD_DRINK,
        NATURE,
        WATER_SPORTS,
        CITY_TOUR,
        WILDLIFE,
        PHOTOGRAPHY,
        WELLNESS,
        NIGHTLIFE,
        SHOPPING,
        ENTERTAINMENT,
        SPORTS,
        EDUCATIONAL,
        OTHER
    }
}