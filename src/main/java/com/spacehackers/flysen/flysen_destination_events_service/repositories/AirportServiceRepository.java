package com.spacehackers.flysen.flysen_destination_events_service.repositories;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.spacehackers.flysen.flysen_destination_events_service.models.AirportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AirportServiceRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "airport_services";

    public String create(AirportService airportService) throws ExecutionException, InterruptedException {
        airportService.setId(UUID.randomUUID().toString());
        airportService.setCreatedAt(Timestamp.now());
        airportService.setUpdatedAt(Timestamp.now());
        airportService.setActive(true);

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(airportService.getId());
        ApiFuture<WriteResult> result = docRef.set(airportService);
        result.get();

        log.info("Created airport service with ID: {}", airportService.getId());
        return airportService.getId();
    }

    public Optional<AirportService> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(AirportService.class));
        }
        return Optional.empty();
    }

    public List<AirportService> findAll(int limit, String lastDocumentId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .limit(limit);

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
                .map(doc -> doc.toObject(AirportService.class))
                .collect(Collectors.toList());
    }

    public List<AirportService> findByAirport(String airportId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("airportId", airportId);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(AirportService.class))
                .collect(Collectors.toList());
    }

    public List<AirportService> findByAirportCode(String airportCode) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("airportCode", airportCode);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(AirportService.class))
                .collect(Collectors.toList());
    }

    public List<AirportService> findByCategory(String airportId, AirportService.ServiceCategory category) 
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("airportId", airportId)
                .whereEqualTo("category", category.name());

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(AirportService.class))
                .sorted(Comparator.comparing(AirportService::getScore, 
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<AirportService> findTopRated(String airportId, int limit) 
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("airportId", airportId);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(AirportService.class))
                .filter(service -> service.getRating() != null)
                .sorted(Comparator.comparing(AirportService::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<AirportService> findByTerminal(String airportId, String terminal) 
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("airportId", airportId)
                .whereEqualTo("terminal", terminal);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> doc.toObject(AirportService.class))
                .collect(Collectors.toList());
    }

    public List<AirportService> search(String airportId, String searchTerm) 
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true)
                .whereEqualTo("airportId", airportId);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        String searchTermLower = searchTerm.toLowerCase();

        return documents.stream()
                .map(doc -> doc.toObject(AirportService.class))
                .filter(service -> 
                    service.getName().toLowerCase().contains(searchTermLower) ||
                    (service.getDescription() != null && 
                     service.getDescription().toLowerCase().contains(searchTermLower)) ||
                    service.getCategory().name().toLowerCase().contains(searchTermLower))
                .collect(Collectors.toList());
    }

    public void update(String id, AirportService airportService) throws ExecutionException, InterruptedException {
        airportService.setUpdatedAt(Timestamp.now());
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> result = docRef.set(airportService, SetOptions.merge());
        result.get();
        log.info("Updated airport service with ID: {}", id);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", false);
        updates.put("updatedAt", Timestamp.now());

        ApiFuture<WriteResult> result = docRef.update(updates);
        result.get();
        log.info("Soft deleted airport service with ID: {}", id);
    }

    // Product-specific methods
    public void addProduct(String serviceId, AirportService.AirportServiceProduct product) 
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(serviceId);
        DocumentSnapshot doc = docRef.get().get();
        
        if (doc.exists()) {
            AirportService service = doc.toObject(AirportService.class);
            if (service != null) {
                List<AirportService.AirportServiceProduct> products = 
                    service.getProducts() != null ? new ArrayList<>(service.getProducts()) : new ArrayList<>();
                
                product.setId(UUID.randomUUID().toString());
                products.add(product);
                
                service.setProducts(products);
                service.setUpdatedAt(Timestamp.now());
                
                docRef.set(service, SetOptions.merge()).get();
                log.info("Added product to service ID: {}", serviceId);
            }
        }
    }

    public void updateProduct(String serviceId, String productId, 
                            AirportService.AirportServiceProduct updatedProduct) 
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(serviceId);
        DocumentSnapshot doc = docRef.get().get();
        
        if (doc.exists()) {
            AirportService service = doc.toObject(AirportService.class);
            if (service != null && service.getProducts() != null) {
                List<AirportService.AirportServiceProduct> products = new ArrayList<>(service.getProducts());
                
                for (int i = 0; i < products.size(); i++) {
                    if (products.get(i).getId().equals(productId)) {
                        updatedProduct.setId(productId);
                        products.set(i, updatedProduct);
                        break;
                    }
                }
                
                service.setProducts(products);
                service.setUpdatedAt(Timestamp.now());
                
                docRef.set(service, SetOptions.merge()).get();
                log.info("Updated product {} in service ID: {}", productId, serviceId);
            }
        }
    }

    public void removeProduct(String serviceId, String productId) 
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(serviceId);
        DocumentSnapshot doc = docRef.get().get();
        
        if (doc.exists()) {
            AirportService service = doc.toObject(AirportService.class);
            if (service != null && service.getProducts() != null) {
                List<AirportService.AirportServiceProduct> products = 
                    service.getProducts().stream()
                        .filter(p -> !p.getId().equals(productId))
                        .collect(Collectors.toList());
                
                service.setProducts(products);
                service.setUpdatedAt(Timestamp.now());
                
                docRef.set(service, SetOptions.merge()).get();
                log.info("Removed product {} from service ID: {}", productId, serviceId);
            }
        }
    }
}