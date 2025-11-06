# Destinations and Events Microservice

A Spring Boot microservice for managing destinations, events, tourist attractions, and activities for the Sembene travel platform.

## Features

- **Destination Management**: CRUD operations for travel destinations with weather integration
- **Event Management**: Handle local events, festivals, and activities
- **Tourist Attractions**: Manage points of interest and attractions
- **External API Integration**:
    - Google Places API for location data
    - Weather API for real-time weather information
    - Firebase Firestore for data persistence
    - Firebase Storage for media files
- **Circuit Breaker Pattern**: Resilience4j for fault tolerance
- **API Documentation**: Swagger/OpenAPI for interactive API docs
- **Health Monitoring**: Spring Boot Actuator endpoints

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Firebase Admin SDK 9.2.0**
- **Google Cloud Storage**
- **Google Maps API**
- **Resilience4j** for circuit breaker
- **Lombok** for reducing boilerplate
- **Maven** for dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- Firebase project with Firestore enabled
- Google Places API key
- Weather API key (OpenWeatherMap)

## Setup

### 1. Clone the repository

```bash
cd destinations-events-service
```

### 2. Configure Firebase

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Firestore Database
3. Enable Firebase Storage
4. Download the service account key JSON file
5. Place it in `src/main/resources/` as `firebase-service-account.json`

### 3. Configure Environment Variables

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` and fill in your API keys:

```properties
FIREBASE_CREDENTIALS_PATH=classpath:firebase-service-account.json
FIREBASE_STORAGE_BUCKET=your-project-id.appspot.com
GOOGLE_PLACES_API_KEY=your_google_places_api_key
WEATHER_API_KEY=your_weather_api_key
```

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run Locally

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8083/api/v1`

## Docker Deployment

### Build Docker Image

```bash
docker build -t destinations-events-service:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

### Stop the service

```bash
docker-compose down
```

## API Endpoints

### Destinations

- `POST /api/v1/destinations` - Create new destination
- `GET /api/v1/destinations/{id}` - Get destination by ID
- `GET /api/v1/destinations` - Get all destinations (paginated)
- `GET /api/v1/destinations/region/{region}` - Get destinations by region
- `GET /api/v1/destinations/popular` - Get popular destinations
- `GET /api/v1/destinations/search?query={query}` - Search destinations
- `PUT /api/v1/destinations/{id}` - Update destination
- `DELETE /api/v1/destinations/{id}` - Delete destination

### Events

- `POST /api/v1/events` - Create new event
- `GET /api/v1/events/{id}` - Get event by ID
- `GET /api/v1/events` - Get all events (paginated)
- `GET /api/v1/events/destination/{destinationId}` - Get events by destination
- `GET /api/v1/events/upcoming?days={days}` - Get upcoming events
- `GET /api/v1/events/featured` - Get featured events
- `GET /api/v1/events/type/{type}` - Get events by type
- `PUT /api/v1/events/{id}` - Update event
- `DELETE /api/v1/events/{id}` - Delete event

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8083/api/v1/swagger-ui.html
```

OpenAPI JSON specification:

```
http://localhost:8083/api/v1/api-docs
```

## Health Checks

Health check endpoint:

```
http://localhost:8083/api/v1/actuator/health
```

Metrics:

```
http://localhost:8083/api/v1/actuator/metrics
```

## Firestore Collections

The service uses the following Firestore collections:

- `destinations` - Travel destinations
- `events` - Local events and festivals
- `tourist_attractions` - Points of interest
- `activities` - Travel activities

## Circuit Breaker Configuration

The service implements circuit breakers for:

- Firestore operations
- Google Places API calls
- Weather API calls

Configuration can be adjusted in `application.yml` under `resilience4j` section.

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `FIREBASE_CREDENTIALS_PATH` | Path to Firebase credentials | `classpath:firebase-service-account.json` |
| `FIREBASE_STORAGE_BUCKET` | Firebase Storage bucket name | `sembene-app.appspot.com` |
| `GOOGLE_PLACES_API_KEY` | Google Places API key | - |
| `WEATHER_API_KEY` | Weather API key | - |

## Project Structure

```
src/
├── main/
│   ├── java/com/sembene/destinations/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exception handlers
│   │   ├── model/           # Domain models
│   │   ├── repository/      # Firestore repositories
│   │   ├── service/         # Business logic
│   │   └── util/            # Utility classes
│   └── resources/
│       ├── application.yml  # Application configuration
│       └── firebase-service-account.json  # Firebase credentials
└── test/                    # Test classes
```

## Error Handling

The service provides standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error message",
  "timestamp": "2025-01-15T10:30:00"
}
```

## Development Tips

1. **Hot Reload**: Use Spring Boot DevTools for automatic restart during development
2. **Logging**: Adjust logging levels in `application.yml`
3. **Testing**: Run tests with `mvn test`
4. **Code Quality**: Use Lombok annotations to reduce boilerplate

## Troubleshooting

### Firebase Connection Issues

- Verify Firebase credentials are correctly placed
- Check Firestore security rules
- Ensure service account has necessary permissions

### API Rate Limiting

- Google Places API has rate limits - implement caching if needed
- Weather API may have request limits based on your plan

### Port Conflicts

If port 8083 is already in use, change it in `application.yml`:

```yaml
server:
  port: 8084
```

## Contributing

1. Follow Java coding conventions
2. Write unit tests for new features
3. Update documentation
4. Use meaningful commit messages

## License

Copyright © 2025 Sembene Travel Platform

## Support

For issues and questions, please contact the development team.