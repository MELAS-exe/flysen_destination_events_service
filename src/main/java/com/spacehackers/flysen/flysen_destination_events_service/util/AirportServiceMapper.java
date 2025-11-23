package com.spacehackers.flysen.flysen_destination_events_service.util;

import com.google.cloud.Timestamp;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.AirportServiceDTO;
import com.spacehackers.flysen.flysen_destination_events_service.models.AirportService;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AirportServiceMapper {

    // Convert Timestamp to ISO 8601 String
    private static String timestampToString(Timestamp timestamp) {
        if (timestamp == null) return null;
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()).toString();
    }

    // Convert ISO 8601 String to Timestamp
    private static Timestamp stringToTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            Instant instant = Instant.parse(dateStr);
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (Exception e) {
            return Timestamp.now();
        }
    }

    // Main AirportService conversions
    public static AirportServiceDTO toDTO(AirportService service) {
        if (service == null) return null;

        return AirportServiceDTO.builder()
                .id(service.getId())
                .airportId(service.getAirportId())
                .airportCode(service.getAirportCode())
                .name(service.getName())
                .category(service.getCategory() != null ? service.getCategory().name() : null)
                .description(service.getDescription())
                .locationMap(service.getLocationMap())
                .openingHours(toOpeningHoursDTO(service.getOpeningHours()))
                .contactInfo(toContactInfoDTO(service.getContactInfo()))
                .images(service.getImages())
                .logo(service.getLogo())
                .score(service.getScore())
                .qrCode(service.getQrCode())
                .products(toProductDTOList(service.getProducts()))
                .terminal(service.getTerminal())
                .gate(service.getGate())
                .floor(service.getFloor())
                .amenities(service.getAmenities())
                .paymentMethods(service.getPaymentMethods())
                .wheelchairAccessible(service.isWheelchairAccessible())
                .averageServiceTime(service.getAverageServiceTime())
                .rating(service.getRating())
                .reviewsCount(service.getReviewsCount())
                .createdAt(timestampToString(service.getCreatedAt()))
                .updatedAt(timestampToString(service.getUpdatedAt()))
                .createdBy(service.getCreatedBy())
                .lastModifiedBy(service.getLastModifiedBy())
                .active(service.isActive())
                .build();
    }

    public static AirportService toModel(AirportServiceDTO dto) {
        if (dto == null) return null;

        return AirportService.builder()
                .id(dto.getId())
                .airportId(dto.getAirportId())
                .airportCode(dto.getAirportCode())
                .name(dto.getName())
                .category(dto.getCategory() != null ? 
                        AirportService.ServiceCategory.valueOf(dto.getCategory()) : null)
                .description(dto.getDescription())
                .locationMap(dto.getLocationMap())
                .openingHours(toOpeningHoursModel(dto.getOpeningHours()))
                .contactInfo(toContactInfoModel(dto.getContactInfo()))
                .images(dto.getImages())
                .logo(dto.getLogo())
                .score(dto.getScore())
                .qrCode(dto.getQrCode())
                .products(toProductModelList(dto.getProducts()))
                .terminal(dto.getTerminal())
                .gate(dto.getGate())
                .floor(dto.getFloor())
                .amenities(dto.getAmenities())
                .paymentMethods(dto.getPaymentMethods())
                .wheelchairAccessible(dto.isWheelchairAccessible())
                .averageServiceTime(dto.getAverageServiceTime())
                .rating(dto.getRating())
                .reviewsCount(dto.getReviewsCount())
                .createdAt(stringToTimestamp(dto.getCreatedAt()))
                .updatedAt(stringToTimestamp(dto.getUpdatedAt()))
                .createdBy(dto.getCreatedBy())
                .lastModifiedBy(dto.getLastModifiedBy())
                .active(dto.isActive())
                .build();
    }

    // Product conversions
    public static AirportServiceDTO.AirportServiceProductDTO toProductDTO(
            AirportService.AirportServiceProduct product) {
        if (product == null) return null;

        return AirportServiceDTO.AirportServiceProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .image(product.getImage())
                .description(product.getDescription())
                .inStock(product.isInStock())
                .tags(product.getTags())
                .estimatedPrepTime(product.getEstimatedPrepTime())
                .nutritionalInfo(product.getNutritionalInfo())
                .build();
    }

    public static AirportService.AirportServiceProduct toProductModel(
            AirportServiceDTO.AirportServiceProductDTO dto) {
        if (dto == null) return null;

        return AirportService.AirportServiceProduct.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .currency(dto.getCurrency())
                .image(dto.getImage())
                .description(dto.getDescription())
                .inStock(dto.isInStock())
                .tags(dto.getTags())
                .estimatedPrepTime(dto.getEstimatedPrepTime())
                .nutritionalInfo(dto.getNutritionalInfo())
                .build();
    }

    private static List<AirportServiceDTO.AirportServiceProductDTO> toProductDTOList(
            List<AirportService.AirportServiceProduct> products) {
        if (products == null) return null;
        return products.stream()
                .map(AirportServiceMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    private static List<AirportService.AirportServiceProduct> toProductModelList(
            List<AirportServiceDTO.AirportServiceProductDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(AirportServiceMapper::toProductModel)
                .collect(Collectors.toList());
    }

    // OpeningHours conversions
    private static Map<String, AirportServiceDTO.OpeningHoursDTO> toOpeningHoursDTO(
            Map<String, AirportService.OpeningHours> openingHours) {
        if (openingHours == null) return null;

        Map<String, AirportServiceDTO.OpeningHoursDTO> dtoMap = new HashMap<>();
        openingHours.forEach((day, hours) -> {
            if (hours != null) {
                dtoMap.put(day, AirportServiceDTO.OpeningHoursDTO.builder()
                        .openTime(hours.getOpenTime())
                        .closeTime(hours.getCloseTime())
                        .closed(hours.isClosed())
                        .open24Hours(hours.isOpen24Hours())
                        .build());
            }
        });
        return dtoMap;
    }

    private static Map<String, AirportService.OpeningHours> toOpeningHoursModel(
            Map<String, AirportServiceDTO.OpeningHoursDTO> dtoMap) {
        if (dtoMap == null) return null;

        Map<String, AirportService.OpeningHours> modelMap = new HashMap<>();
        dtoMap.forEach((day, dto) -> {
            if (dto != null) {
                modelMap.put(day, AirportService.OpeningHours.builder()
                        .openTime(dto.getOpenTime())
                        .closeTime(dto.getCloseTime())
                        .closed(dto.isClosed())
                        .open24Hours(dto.isOpen24Hours())
                        .build());
            }
        });
        return modelMap;
    }

    // ContactInfo conversions
    private static AirportServiceDTO.ContactInfoDTO toContactInfoDTO(
            AirportService.ContactInfo contactInfo) {
        if (contactInfo == null) return null;

        return AirportServiceDTO.ContactInfoDTO.builder()
                .email(contactInfo.getEmail())
                .phone(contactInfo.getPhone())
                .website(contactInfo.getWebsite())
                .emergencyContact(contactInfo.getEmergencyContact())
                .build();
    }

    private static AirportService.ContactInfo toContactInfoModel(
            AirportServiceDTO.ContactInfoDTO dto) {
        if (dto == null) return null;

        return AirportService.ContactInfo.builder()
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .website(dto.getWebsite())
                .emergencyContact(dto.getEmergencyContact())
                .build();
    }
}