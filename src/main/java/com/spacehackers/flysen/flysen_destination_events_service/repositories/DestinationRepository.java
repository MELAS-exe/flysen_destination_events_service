package com.spacehackers.flysen.flysen_destination_events_service.repositories;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.spacehackers.flysen.flysen_destination_events_service.models.Destination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DestinationRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "destinations";

    public String create(Destination destination) throws ExecutionException, InterruptedException {
        destination.setId(UUID.randomUUID().toString());
        destination.setCreatedAt(Timestamp.now());
        destination.setUpdatedAt(Timestamp.now());
        destination.setActive(true);

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(destination.getId());
        ApiFuture<WriteResult> result = docRef.set(destination);
        result.get();

        log.info("Created destination with ID: {}", destination.getId());
        return destination.getId();
    }

    public Optional<Destination> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Destination.class));
        }
        return Optional.empty();
    }

    public List<Destination> findAll(int limit, String lastDocumentId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .limit(limit);

        // Removed orderBy to avoid needing composite index
        // If you want ordering, create the index in Firebase Console

        if (lastDocumentId != null && !lastDocumentId.isEmpty()) {
            DocumentSnapshot lastDoc = firestore.collection(COLLECTION_NAME)
                    .document(lastDocumentId)
                    .get()
                    .get();
            query = query.startAfter(lastDoc);
        }

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Destination.class))
                .collect(Collectors.toList());
    }

    public List<Destination> findByRegion(String region) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("region", region);

        // Removed orderBy to avoid needing index

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Destination.class))
                .collect(Collectors.toList());
    }

    public List<Destination> findPopular(int limit) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .orderBy("popularityScore", Query.Direction.DESCENDING)
                .limit(limit);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Destination.class))
                .collect(Collectors.toList());
    }

    public void update(String id, Destination destination) throws ExecutionException, InterruptedException {
        destination.setUpdatedAt(Timestamp.now());
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> result = docRef.set(destination, SetOptions.merge());
        result.get();
        log.info("Updated destination with ID: {}", id);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", false);
        updates.put("updatedAt", Timestamp.now());

        ApiFuture<WriteResult> result = docRef.update(updates);
        result.get();
        log.info("Soft deleted destination with ID: {}", id);
    }

    public List<Destination> search(String searchTerm) throws ExecutionException, InterruptedException {
        // Firestore doesn't support full-text search, so we'll implement a simple prefix search
        String searchTermLower = searchTerm.toLowerCase();

        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true);

        // Removed orderBy to avoid needing index

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Destination.class))
                .filter(dest -> dest.getName().toLowerCase().contains(searchTermLower) ||
                        (dest.getDescription() != null && dest.getDescription().toLowerCase().contains(searchTermLower)))
                .collect(Collectors.toList());
    }
}