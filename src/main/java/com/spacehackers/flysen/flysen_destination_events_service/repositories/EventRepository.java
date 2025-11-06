package com.spacehackers.flysen.flysen_destination_events_service.repositories;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.spacehackers.flysen.flysen_destination_events_service.models.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "events";

    public String create(Event event) throws ExecutionException, InterruptedException {
        event.setId(UUID.randomUUID().toString());
        event.setCreatedAt(Timestamp.now());
        event.setUpdatedAt(Timestamp.now());
        event.setActive(true);
        event.setStatus(Event.EventStatus.SCHEDULED);

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(event.getId());
        ApiFuture<WriteResult> result = docRef.set(event);
        result.get();

        log.info("Created event with ID: {}", event.getId());
        return event.getId();
    }

    public Optional<Event> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Event.class));
        }
        return Optional.empty();
    }

    public List<Event> findAll(int limit, String lastDocumentId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .limit(limit);

        // Removed orderBy to avoid needing index

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
                .map(doc -> doc.toObject(Event.class))
                .collect(Collectors.toList());
    }

    public List<Event> findByDestination(String destinationId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("destinationId", destinationId);

        // Removed orderBy to avoid needing index

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Event.class))
                .collect(Collectors.toList());
    }

    public List<Event> findUpcoming(Timestamp startDate, Timestamp endDate, int limit)
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .limit(limit);

        // Removed orderBy to avoid needing composite index

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Event.class))
                .sorted(Comparator.comparing(Event::getDate)) // Sort in memory
                .collect(Collectors.toList());
    }

    public List<Event> findFeatured(int limit) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("featured", true)
                .limit(limit);

        // Removed orderBy to avoid needing index

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Event.class))
                .sorted(Comparator.comparing(Event::getDate)) // Sort in memory
                .collect(Collectors.toList());
    }

    public List<Event> findByType(Event.EventType type) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("type", type.name());

        // Removed orderBy to avoid needing index

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(Event.class))
                .sorted(Comparator.comparing(Event::getDate)) // Sort in memory
                .collect(Collectors.toList());
    }

    public void update(String id, Event event) throws ExecutionException, InterruptedException {
        event.setUpdatedAt(Timestamp.now());
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> result = docRef.set(event, SetOptions.merge());
        result.get();
        log.info("Updated event with ID: {}", id);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", false);
        updates.put("updatedAt", Timestamp.now());

        ApiFuture<WriteResult> result = docRef.update(updates);
        result.get();
        log.info("Soft deleted event with ID: {}", id);
    }
}