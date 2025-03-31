# Key-Value Store Service

A lightweight, high-performance in-memory key-value store built with Spring Boot. This project provides a simple yet robust REST API for storing and retrieving key-value pairs with optimized performance characteristics.

## Features

- **Fast In-Memory Storage**: Utilizes ConcurrentHashMap for thread-safe operations with low latency
- **RESTful API**: Clean API endpoints for CRUD operations on key-value pairs
- **Input Validation**: Prevents oversized keys and values (maximum 256 characters)
- **Dockerized**: Containerized for easy deployment and scalability
- **Java 17**: Built with modern Java features for optimal performance

## Technology Stack

- Java 17
- Spring Boot 3.4.4
- Maven
- Docker
- Lombok for reduced boilerplate code
- JUnit for testing

## Project Walkthrough

### Architecture

The project follows a clean, layered architecture:

1. **Controller Layer**: Handles HTTP requests and responses, input validation
2. **Service Layer**: Contains business logic and manages the key-value store
3. **Model Layer**: Defines data structures for the application

### How It Works

1. A client sends a PUT request with a key-value pair to `/put`
2. The `CacheController` validates the input (key and value must be < 256 chars)
3. The validated data is passed to `CacheService`
4. `CacheService` stores the pair in a thread-safe ConcurrentHashMap
5. When a client requests a key via a GET to `/get?key=example_key`:
   - The controller forwards the request to the service
   - If the key exists, a CacheResponse with status "OK" and the value is returned
   - If not found, a response with status "ERROR" is returned

### Key Components

- **KeyvalueApplication.java**: The main Spring Boot application entry point
- **CacheController.java**: REST API endpoints for managing cache operations
- **CacheService.java**: Core implementation of the key-value store using ConcurrentHashMap
- **CacheResponse.java**: Model class for standardized API responses

## API Endpoints

### Store a Key-Value Pair

```
POST /put
```

Request Body:
```json
{
  "key": "example_key",
  "value": "example_value"
}
```

Response:
```json
{
  "status": "OK",
  "message": "Key inserted/updated successfully."
}
```

### Retrieve a Value by Key

```
GET /get?key=example_key
```

Response (Success):
```json
{
  "status": "OK",
  "message": null,
  "key": "example_key",
  "value": "example_value"
}
```

Response (Key Not Found):
```json
{
  "status": "ERROR",
  "message": "Key not found.",
  "key": null,
  "value": null
}
```

## Getting Started

### Prerequisites

- Docker
- Java 17 (for development)
- Maven (for development)

### Running with Docker

1. Build the Docker image:
   ```bash
   docker build -t key-value-store:latest .
   ```

2. Run the container:
   ```bash
   docker run -p 7171:7171 key-value-store:latest
   ```

3. The service will be available at http://127.0.0.1:7171

### Development Setup

1. Clone the repository
   ```bash
   git clone https://github.com/shrimay18/key-value-cache.git
   cd key-value-cache
   ```

2. Build with Maven
   ```bash
   ./mvnw clean package
   ```

3. Run the application
   ```bash
   ./mvnw spring-boot:run
   ```

## Optimizations & Best Practices

### Performance Optimizations

1. **ConcurrentHashMap Implementation**: 
   - Selected over HashMap for thread-safety without compromising performance
   - Provides lock-free read operations and lock segmentation for writes
   - Scales well with multiple CPU cores handling concurrent requests

2. **Minimal Object Creation**: 
   - Reused model objects where possible to minimize garbage collection pressure
   - Used primitive types and String pooling to reduce memory overhead
   - Implemented efficient constructors to minimize object initialization costs

3. **Early Validation**: 
   - Validate inputs at the controller level before proceeding to service operations
   - Size constraints (max 256 chars) prevent memory abuse and ensure quick response times
   - Null checks prevent unnecessary processing of invalid requests

4. **Defensive Copying**: 
   - The `getAll()` method returns an immutable copy of the cache, preventing external modification
   - Prevents memory leaks and ensures data integrity

5. **Optimized Error Handling**: 
   - Fast path for error cases, avoiding unnecessary processing
   - Standardized error responses with clear status codes and messages
   - No expensive exception stack traces for expected error conditions

### Code Quality Best Practices

1. **Separation of Concerns**: 
   - Clear distinction between controller, service, and model layers
   - Each class has a single responsibility
   - Dependency injection via constructor for better testability

2. **Immutable Data**: 
   - Response objects are effectively immutable once constructed
   - Makes code more thread-safe and easier to reason about

3. **Consistent API Design**: 
   - Standardized response format across all endpoints
   - Clear status codes and messages for error conditions
   - RESTful URL patterns and HTTP methods

4. **Clean Code Principles**:
   - Descriptive method and variable names
   - Small, focused methods with single responsibilities
   - Minimal code duplication

5. **Lombok Usage**: 
   - Reduced boilerplate code with @Data, @AllArgsConstructor, etc.
   - Improved readability and maintainability
   - Less error-prone than manual implementation of equals/hashCode

### Docker Optimizations

1. **Multi-stage Build**:
   - Build stage uses Maven image to compile the application
   - Runtime stage uses minimal JRE image (adoptopenjdk:17-jre-hotspot)
   - Final image is significantly smaller (~150MB vs ~500MB)

2. **Layer Optimization**:
   - Dependencies are copied and installed before code is copied
   - Leverages Docker's layer caching for faster builds
   - Example:
     ```dockerfile
     COPY pom.xml .
     RUN mvn dependency:go-offline
     
     COPY src/ ./src/
     RUN mvn package
     ```

3. **Non-root User**: 
   - Created and used a dedicated non-root user for running the application
   - Enhances security by limiting container privileges
   - Example:
     ```dockerfile
     RUN addgroup --system --gid 1001 appuser && \
         adduser --system --uid 1001 --gid 1001 appuser
     USER appuser
     ```

4. **Environment Configuration**: 
   - Used environment variables for configurable settings
   - Enables runtime customization without rebuilding the image
   - Example:
     ```dockerfile
     ENV JAVA_OPTS="-Xms256m -Xmx512m"
     ```

5. **Health Checks**: 
   - Implemented Docker health checks for the service
   - Enables container orchestration systems to monitor service health
   - Example:
     ```dockerfile
     HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8080/actuator/health || exit 1
     ```

6. **Proper Entry Point**:
   - Used ENTRYPOINT for running the application
   - Used CMD for default arguments that can be overridden
   - Example:
     ```dockerfile
     ENTRYPOINT ["java", "-jar", "app.jar"]
     CMD ["--spring.profiles.active=prod"]
     ```

## Testing

The project includes comprehensive unit and integration tests to ensure functionality and performance:

```bash
# Run tests
./mvnw test
```

Test coverage includes:
- Unit tests for service layer logic
- Integration tests for API endpoints
- Performance tests for concurrency handling
- Edge case testing for validation logic

## Future Enhancements

- Persistence layer for data durability
- Caching TTL (Time-To-Live) support
- Cluster support for distributed deployment
- Authentication and authorization
- API rate limiting
- Extended API for batch operations

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

Shrimay

---
