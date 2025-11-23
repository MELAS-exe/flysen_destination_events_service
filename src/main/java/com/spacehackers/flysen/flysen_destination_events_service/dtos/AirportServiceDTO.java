package com.spacehackers.flysen.flysen_destination_events_service.dtos;

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
public class AirportServiceDTO {
    
    private String id;
    private String airportId;
    private String airportCode;
    private String name;
    private String category;  // ServiceCategory as String
    private String description;
    private String locationMap;
    private Map<String, OpeningHoursDTO> openingHours;
    private ContactInfoDTO contactInfo;
    private List<String> images;
    private String logo;
    private Double score;
    private String qrCode;
    
    // Products/Items offered by this service
    private List<AirportServiceProductDTO> products;
    
    // Location within airport
    private String terminal;
    private String gate;
    private String floor;
    
    // Additional info
    private List<String> amenities;
    private List<String> paymentMethods;
    private boolean wheelchairAccessible;
    private Integer averageServiceTime; // in minutes
    
    // Rating
    private Double rating;
    private Integer reviewsCount;
    
    // Metadata
    private String createdAt;  // ISO 8601 format
    private String updatedAt;  // ISO 8601 format
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportServiceProductDTO {
        private String id;
        private String name;
        private Double price;
        private String currency;
        private String image;
        private String description;
        private boolean inStock;
        private List<String> tags;
        private Integer estimatedPrepTime; // in minutes
        private Map<String, String> nutritionalInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningHoursDTO {
        private String openTime;
        private String closeTime;
        private boolean closed;
        private boolean open24Hours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfoDTO {
        private String email;
        private String phone;
        private String website;
        private String emergencyContact;
    }
}