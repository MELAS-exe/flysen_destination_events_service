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
public class AirportService {

    private String id;
    private String airportId;
    private String airportCode;
    private String name;
    private ServiceCategory category;
    private String description;
    private String locationMap;
    private Map<String, OpeningHours> openingHours;
    private ContactInfo contactInfo;
    private List<String> images;
    private String logo;
    private Double score;
    private String qrCode;
    
    // Products/Items offered by this service
    private List<AirportServiceProduct> products;
    
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
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;

    public enum ServiceCategory {
        RESTAURANT,
        CAFE,
        DUTY_FREE,
        RETAIL,
        LOUNGE,
        BANK_ATM,
        CURRENCY_EXCHANGE,
        INFORMATION_DESK,
        BAGGAGE_SERVICES,
        CAR_RENTAL,
        PHARMACY,
        SPA_WELLNESS,
        BUSINESS_CENTER,
        CHARGING_STATION,
        WIFI_ZONE,
        PRAYER_ROOM,
        MEDICAL_CENTER,
        LOST_AND_FOUND,
        CUSTOMS,
        IMMIGRATION,
        SECURITY,
        VIP_SERVICES,
        HOTEL,
        TRANSPORTATION,
        OTHER
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportServiceProduct {
        private String id;
        private String name;
        private Double price;
        private String currency;
        private String image;
        private String description;
        private boolean inStock;
        private List<String> tags;
        private Integer estimatedPrepTime; // in minutes
        private Map<String, String> nutritionalInfo; // for food items
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningHours {
        private String openTime;   // Store as String (e.g., "09:00")
        private String closeTime;  // Store as String (e.g., "17:00")
        private boolean closed;
        private boolean open24Hours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String email;
        private String phone;
        private String website;
        private String emergencyContact;
    }
}