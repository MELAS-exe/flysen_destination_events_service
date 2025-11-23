package com.spacehackers.flysen.flysen_destination_events_service.controllers;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.spacehackers.flysen.flysen_destination_events_service.dtos.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Media upload and management APIs")
public class ImageUploadController {

    private final Storage storage;

    @Value("${firebase.storage-bucket}")
    private String bucketName;

    @PostMapping(value = "/upload/destination-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload destination images")
    public ResponseEntity<ApiResponse<List<String>>> uploadDestinationImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("destinationId") String destinationId) {
        
        log.info("Uploading {} images for destination: {}", files.size(), destinationId);
        
        List<String> imageUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                String imageUrl = uploadImage(file, "destinations/" + destinationId);
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                log.error("Error uploading image: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.<List<String>>builder()
                                .success(false)
                                .message("Failed to upload image: " + file.getOriginalFilename())
                                .error(e.getMessage())
                                .build());
            }
        }
        
        return ResponseEntity.ok(
                ApiResponse.<List<String>>builder()
                        .success(true)
                        .message("Images uploaded successfully")
                        .data(imageUrls)
                        .build());
    }

    @PostMapping(value = "/upload/event-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload event images")
    public ResponseEntity<ApiResponse<List<String>>> uploadEventImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("eventId") String eventId) {
        
        log.info("Uploading {} images for event: {}", files.size(), eventId);
        
        List<String> imageUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                String imageUrl = uploadImage(file, "events/" + eventId);
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                log.error("Error uploading image: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.<List<String>>builder()
                                .success(false)
                                .message("Failed to upload image: " + file.getOriginalFilename())
                                .error(e.getMessage())
                                .build());
            }
        }
        
        return ResponseEntity.ok(
                ApiResponse.<List<String>>builder()
                        .success(true)
                        .message("Images uploaded successfully")
                        .data(imageUrls)
                        .build());
    }

    @PostMapping(value = "/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a single image")
    public ResponseEntity<ApiResponse<String>> uploadSingleImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        
        try {
            String imageUrl = uploadImage(file, folder);
            
            return ResponseEntity.ok(
                    ApiResponse.<String>builder()
                            .success(true)
                            .message("Image uploaded successfully")
                            .data(imageUrl)
                            .build());
        } catch (IOException e) {
            log.error("Error uploading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Failed to upload image")
                            .error(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete an image from storage")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            // Extract the blob name from the URL
            String blobName = extractBlobNameFromUrl(imageUrl);
            
            if (blobName != null) {
                storage.delete(bucketName, blobName);
                
                return ResponseEntity.ok(
                        ApiResponse.<Void>builder()
                                .success(true)
                                .message("Image deleted successfully")
                                .build());
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<Void>builder()
                                .success(false)
                                .message("Invalid image URL")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to delete image")
                            .error(e.getMessage())
                            .build());
        }
    }

    private String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        String filename = UUID.randomUUID().toString() + extension;
        String blobName = folder + "/" + filename;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName)
                .setContentType(contentType)
                .build();

        Blob blob = storage.create(blobInfo, file.getBytes());

        // Optionally make it public
        // blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        // Return Firebase public URL
        String encodedPath = java.net.URLEncoder.encode(blobName, java.nio.charset.StandardCharsets.UTF_8);
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                encodedPath
        );
    }

    private String extractBlobNameFromUrl(String imageUrl) {
        // Extract blob name from Firebase Storage URL
        // Format: https://storage.googleapis.com/bucket-name/path/to/file.jpg
        if (imageUrl != null && imageUrl.contains(bucketName)) {
            int index = imageUrl.indexOf(bucketName) + bucketName.length() + 1;
            if (index < imageUrl.length()) {
                return imageUrl.substring(index);
            }
        }
        return null;
    }
}