package com.spacehackers.flysen.flysen_destination_events_service.util;

import com.google.cloud.Timestamp;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.DestinationDTO;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.EventDTO;
import com.spacehackers.flysen.flysen_destination_events_service.models.Destination;
import com.spacehackers.flysen.flysen_destination_events_service.models.Event;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class ModelMapper {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    
    // Convert Timestamp to ISO 8601 String
    public static String timestampToString(Timestamp timestamp) {
        if (timestamp == null) return null;
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()).toString();
    }
    
    // Convert ISO 8601 String to Timestamp
    public static Timestamp stringToTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            Instant instant = Instant.parse(dateStr);
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (Exception e) {
            // If parsing fails, return current timestamp
            return Timestamp.now();
        }
    }
    
    // Destination conversions
    public static DestinationDTO toDTO(Destination destination) {
        if (destination == null) return null;
        
        return DestinationDTO.builder()
                .id(destination.getId())
                .name(destination.getName())
                .region(destination.getRegion())
                .nearestAirportId(destination.getNearestAirportId())
                .nearestAirportCode(destination.getNearestAirportCode())
                .description(destination.getDescription())
                .highlights(destination.getHighlights())
                .images(destination.getImages())
                .videos(destination.getVideos())
                .virtualTourUrl(destination.getVirtualTourUrl())
                .bestSeason(destination.getBestSeason())
                .averageStayDuration(destination.getAverageStayDuration())
                .popularityScore(destination.getPopularityScore())
                .latitude(destination.getLatitude())
                .longitude(destination.getLongitude())
                .currentWeather(toWeatherDTO(destination.getCurrentWeather()))
                .stats(toStatsDTO(destination.getStats()))
                .createdAt(timestampToString(destination.getCreatedAt()))
                .updatedAt(timestampToString(destination.getUpdatedAt()))
                .createdBy(destination.getCreatedBy())
                .lastModifiedBy(destination.getLastModifiedBy())
                .active(destination.isActive())
                .build();
    }
    
    public static Destination toModel(DestinationDTO dto) {
        if (dto == null) return null;
        
        return Destination.builder()
                .id(dto.getId())
                .name(dto.getName())
                .region(dto.getRegion())
                .nearestAirportId(dto.getNearestAirportId())
                .nearestAirportCode(dto.getNearestAirportCode())
                .description(dto.getDescription())
                .highlights(dto.getHighlights())
                .images(dto.getImages())
                .videos(dto.getVideos())
                .virtualTourUrl(dto.getVirtualTourUrl())
                .bestSeason(dto.getBestSeason())
                .averageStayDuration(dto.getAverageStayDuration())
                .popularityScore(dto.getPopularityScore())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .currentWeather(toWeatherModel(dto.getCurrentWeather()))
                .stats(toStatsModel(dto.getStats()))
                .createdAt(stringToTimestamp(dto.getCreatedAt()))
                .updatedAt(stringToTimestamp(dto.getUpdatedAt()))
                .createdBy(dto.getCreatedBy())
                .lastModifiedBy(dto.getLastModifiedBy())
                .active(dto.isActive())
                .build();
    }
    
    private static DestinationDTO.WeatherInfoDTO toWeatherDTO(Destination.WeatherInfo weather) {
        if (weather == null) return null;
        return DestinationDTO.WeatherInfoDTO.builder()
                .temperature(weather.getTemperature())
                .condition(weather.getCondition())
                .humidity(weather.getHumidity())
                .description(weather.getDescription())
                .lastUpdated(timestampToString(weather.getLastUpdated()))
                .build();
    }
    
    private static Destination.WeatherInfo toWeatherModel(DestinationDTO.WeatherInfoDTO dto) {
        if (dto == null) return null;
        return Destination.WeatherInfo.builder()
                .temperature(dto.getTemperature())
                .condition(dto.getCondition())
                .humidity(dto.getHumidity())
                .description(dto.getDescription())
                .lastUpdated(stringToTimestamp(dto.getLastUpdated()))
                .build();
    }
    
    private static DestinationDTO.DestinationStatsDTO toStatsDTO(Destination.DestinationStats stats) {
        if (stats == null) return null;
        return DestinationDTO.DestinationStatsDTO.builder()
                .totalAttractions(stats.getTotalAttractions())
                .totalAccommodations(stats.getTotalAccommodations())
                .totalActivities(stats.getTotalActivities())
                .totalEvents(stats.getTotalEvents())
                .averageRating(stats.getAverageRating())
                .totalReviews(stats.getTotalReviews())
                .monthlyVisitors(stats.getMonthlyVisitors())
                .build();
    }
    
    private static Destination.DestinationStats toStatsModel(DestinationDTO.DestinationStatsDTO dto) {
        if (dto == null) return null;
        return Destination.DestinationStats.builder()
                .totalAttractions(dto.getTotalAttractions())
                .totalAccommodations(dto.getTotalAccommodations())
                .totalActivities(dto.getTotalActivities())
                .totalEvents(dto.getTotalEvents())
                .averageRating(dto.getAverageRating())
                .totalReviews(dto.getTotalReviews())
                .monthlyVisitors(dto.getMonthlyVisitors())
                .build();
    }
    
    // Event conversions
    public static EventDTO toDTO(Event event) {
        if (event == null) return null;
        
        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .type(event.getType() != null ? event.getType().name() : null)
                .destinationId(event.getDestinationId())
                .destinationName(event.getDestinationName())
                .date(timestampToString(event.getDate()))
                .endDate(timestampToString(event.getEndDate()))
                .venue(event.getVenue())
                .description(event.getDescription())
                .images(event.getImages())
                .ticketPrice(event.getTicketPrice())
                .capacity(event.getCapacity())
                .remainingCapacity(event.getRemainingCapacity())
                .featured(event.isFeatured())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .address(event.getAddress())
                .organizer(toOrganizerDTO(event.getOrganizer()))
                .stats(toEventStatsDTO(event.getStats()))
                .createdAt(timestampToString(event.getCreatedAt()))
                .updatedAt(timestampToString(event.getUpdatedAt()))
                .createdBy(event.getCreatedBy())
                .lastModifiedBy(event.getLastModifiedBy())
                .active(event.isActive())
                .status(event.getStatus() != null ? event.getStatus().name() : null)
                .build();
    }
    
    public static Event toModel(EventDTO dto) {
        if (dto == null) return null;
        
        return Event.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType() != null ? Event.EventType.valueOf(dto.getType()) : null)
                .destinationId(dto.getDestinationId())
                .destinationName(dto.getDestinationName())
                .date(stringToTimestamp(dto.getDate()))
                .endDate(stringToTimestamp(dto.getEndDate()))
                .venue(dto.getVenue())
                .description(dto.getDescription())
                .images(dto.getImages())
                .ticketPrice(dto.getTicketPrice())
                .capacity(dto.getCapacity())
                .remainingCapacity(dto.getRemainingCapacity())
                .featured(dto.isFeatured())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .organizer(toOrganizerModel(dto.getOrganizer()))
                .stats(toEventStatsModel(dto.getStats()))
                .createdAt(stringToTimestamp(dto.getCreatedAt()))
                .updatedAt(stringToTimestamp(dto.getUpdatedAt()))
                .createdBy(dto.getCreatedBy())
                .lastModifiedBy(dto.getLastModifiedBy())
                .active(dto.isActive())
                .status(dto.getStatus() != null ? Event.EventStatus.valueOf(dto.getStatus()) : null)
                .build();
    }
    
    private static EventDTO.OrganizerInfoDTO toOrganizerDTO(Event.OrganizerInfo organizer) {
        if (organizer == null) return null;
        return EventDTO.OrganizerInfoDTO.builder()
                .name(organizer.getName())
                .email(organizer.getEmail())
                .phone(organizer.getPhone())
                .website(organizer.getWebsite())
                .build();
    }
    
    private static Event.OrganizerInfo toOrganizerModel(EventDTO.OrganizerInfoDTO dto) {
        if (dto == null) return null;
        return Event.OrganizerInfo.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .website(dto.getWebsite())
                .build();
    }
    
    private static EventDTO.EventStatsDTO toEventStatsDTO(Event.EventStats stats) {
        if (stats == null) return null;
        return EventDTO.EventStatsDTO.builder()
                .totalBookings(stats.getTotalBookings())
                .totalViews(stats.getTotalViews())
                .averageRating(stats.getAverageRating())
                .totalReviews(stats.getTotalReviews())
                .expressOffersCount(stats.getExpressOffersCount())
                .build();
    }
    
    private static Event.EventStats toEventStatsModel(EventDTO.EventStatsDTO dto) {
        if (dto == null) return null;
        return Event.EventStats.builder()
                .totalBookings(dto.getTotalBookings())
                .totalViews(dto.getTotalViews())
                .averageRating(dto.getAverageRating())
                .totalReviews(dto.getTotalReviews())
                .expressOffersCount(dto.getExpressOffersCount())
                .build();
    }
}