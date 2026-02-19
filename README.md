# PetMall Backend

Spring Boot backend for a pet adoption platform with support for adopters, staff, managers, shelters, animals, applications, and adoptions.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Web + Spring Data JPA + Spring Security
- PostgreSQL
- JWT (JJWT)
- OpenAPI/Swagger UI (springdoc)
- Gradle

## Core Features

- User authentication (`/auth/signup`, `/auth/login`) with JWT token issuance.
- Role-oriented domain model (`ADOPTER`, `STAFF`, `MANAGER`) and profile flows.
- Shelter management (create/update/list shelters).
- Animal management (add/update/list/filter and availability checks).
- Adoption application flow (submit, list, and update status).
- Animal event tracking (e.g. intake/status related events).
- Starter data initializer that creates default manager/staff accounts if missing.

## Project Structure

```text
src/main/java/com/petadoption
├── config/         # security, web config, swagger, bootstrap data
├── controller/     # REST endpoints
├── dto/            # request/response contracts
├── entity/         # JPA entities
├── enums/          # enum definitions (roles/statuses)
├── repository/     # Spring Data repositories
├── security/       # JWT utility + filter
└── service/        # business logic
```

There are also CLI entry points in the root package:
- `PetMallCLI`
- `ManagerCLI`
- `StaffCLI`

## Prerequisites

- Java 17+
- PostgreSQL running locally
- Gradle wrapper is included (`./gradlew`)

## Configuration

Current database settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/petadoption_db
spring.datasource.username=sanjar
spring.datasource.password=12345
```

Update these values for your environment before running in production/dev team environments.

## Run Locally

1. Create DB in PostgreSQL:

```sql
CREATE DATABASE petadoption_db;
```

2. Start the app:

```bash
./gradlew bootRun
```

3. API base URL:

```text
http://localhost:8080
```

## API Documentation (Swagger)

After startup, open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Default Seed Accounts

On first run, `DataInitializer` creates:

- Manager: `manager@gmail.com` / `123`
- Staff: `staff1@gmail.com` / `password123`

## Endpoint Overview

### Auth
- `POST /auth/signup`
- `POST /auth/login`

### Adopter
- `GET /adopter/me`
- `PUT /adopter/update`
- `PUT /adopter/updatePassword`
- `GET /adopter/getAdoptions`

### Staff
- `POST /staff/create`
- `GET /staff/me`
- `PUT /staff/update/profile`

### Shelter
- `GET /shelter/getAll`
- `POST /shelter/create`
- `PUT /shelter/update/{id}`

### Animals
- `GET /animals?shelterId=...&status=...&name=...&species=...&age=...`
- `GET /animals/{id}/availability`
- `GET /animals/all`
- `POST /animals/add`
- `PUT /animals/update/{id}`

### Animal Events
- `POST /animal-events/add`

### Applications
- `GET /application/getAll`
- `POST /application?animalId=...`
- `GET /application/getAllApplications`
- `PUT /application/update-status`

## Testing

Run all tests:

```bash
./gradlew test
```

## Security Notes

- JWT tokens are generated and consumed in service/controller logic.
- Current `SecurityConfig` is permissive (`anyRequest().permitAll()`).
- For production hardening, lock routes by role and wire JWT filter enforcement in the security chain.

## Quick cURL Example

Login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"manager@gmail.com","password":"123"}'
```

Use token:

```bash
curl http://localhost:8080/adopter/me \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Known Improvement Areas

- Move DB secrets out of source control (env variables / profiles).
- Enforce authorization at Spring Security layer.
- Add broader integration tests for key workflows.
- Add API examples for every endpoint in Swagger annotations.

---
If you want, I can also generate a **frontend-friendly API reference section** (sample request/response JSON for each endpoint) in a second pass.
