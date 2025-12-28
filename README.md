# Car Rental Backend (Spring Boot 3 + JWT)

Backend system for a car rental service. Supports car management by owners, bookings by clients, admin management endpoints, and JWT-based stateless authentication with role-based authorization.

## Tech Stack
- Java 17
- Spring Boot 3 (Spring MVC, Spring Security, Spring Data JPA)
- PostgreSQL
- JWT (stateless authentication)
- OpenAPI / Swagger UI
- JUnit 5, Testcontainers

## Roles
- **CLIENT**: browse available cars, create bookings, view own bookings, add reviews
- **OWNER**: create/update/delete own cars, manage booking status for own cars
- **ADMIN**: manage/view users and cars via admin endpoints

## API Overview

### Auth
- `POST /auth/register` — register user
- `POST /auth/login` — login (returns JWT)
- `POST /auth/logout` — logout (stateless)

### Users
- `GET /users/me` — get current user
- `PUT /users/me` — update current user
- `DELETE /users/me` — delete/deactivate current user

### Cars
- `POST /cars` — create car (OWNER)
- `GET /cars` — list available cars
- `GET /cars/{id}` — get car by id
- `PUT /cars/{id}` — update car (OWNER)
- `DELETE /cars/{id}` — delete car (OWNER)

### Bookings
- `POST /bookings` — create booking (CLIENT)
- `GET /bookings/{id}` — get booking by id
- `PUT /bookings/{id}/status` — update booking status (OWNER)
- `GET /users/me/bookings` — list current user's bookings

### Reviews
- `POST /cars/{id}/reviews` — add review (CLIENT)
- `GET /cars/{id}/reviews` — list reviews for a car

### Admin
- `GET /admin/users` — list/manage users (ADMIN)
- `GET /admin/cars` — list/manage cars (ADMIN)

## Business Rules
- Booking interval uses **[startDate, endDate)** semantics (endDate is exclusive).
- Prevents **overlapping bookings** for the same car for active statuses.
- Prevents booking if the car is **UNAVAILABLE**.
- Returns proper HTTP codes (e.g., `409 CONFLICT` for booking conflicts).

## Getting Started

### Prerequisites
- Java 17
- Maven 3+
- PostgreSQL database

### Configure Database
Update credentials in:
- `src/main/resources/application.yml`

Common properties:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

### Run Application
```bash
mvn spring-boot:run
```

## Swagger UI

Open:
- http://localhost:8080/swagger-ui/index.html

### How to authorize (JWT)
1. Call `POST /auth/login` and copy the returned token.
2. Click **Authorize** (lock icon) in Swagger.
3. Paste:
   ```
   Bearer <your_token>
   ```
4. Execute secured endpoints based on your role.

## Testing

### Run All Tests
```bash
mvn test
```

### Run a Specific Test
```bash
mvn -Dtest=BookingServiceIntegrationTest test
```

Integration tests use **Testcontainers** to start a real PostgreSQL instance automatically.

## Project Structure (High Level)
- `controller/` — REST endpoints
- `service/` + `service/impl/` — business logic
- `repository/` — Spring Data JPA repositories
- `entity/` — JPA entities
- `model/` — enums (roles/statuses)
- `dto/` — request/response API models
- `mapper/` — DTO ↔ Entity mapping
- `security/` — JWT config, filter, UserDetails
- `exception/` — global exception handling
