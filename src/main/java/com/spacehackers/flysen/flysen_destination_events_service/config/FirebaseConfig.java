package com.spacehackers.flysen.flysen_destination_events_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials-path}")
    private Resource credentialsPath;

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = credentialsPath.getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(storageBucket)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase has been initialized successfully");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    public Storage storage() {
        try {
            InputStream serviceAccount = credentialsPath.getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            
            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            log.error("Failed to initialize Cloud Storage: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Cloud Storage", e);
        }
    }

    @Bean
    public com.google.cloud.storage.Bucket storageBucket(Storage storage) {
        return StorageClient.getInstance().bucket();
    }
}