# Adoption Service

Adoption Service is a Spring Boot 3 application for managing pets, pet pictures, and adoption applications for an animal rescue organization.

## Features

- Manage pets with status, attributes, and multiple stored pictures
- Store pet pictures as binary data in PostgreSQL
- Support full pet replacement updates and partial patch updates with picture-list merging
- Submit and review adoption applications
- Basic HTTP security with role-based access for organization members
- OpenAPI 3 documentation via SpringDoc

## Project structure

- `src/main/java` – application code, controllers, services, repositories, models, and DTOs
- `src/main/resources` – application configuration
- `src/test/java` – integration and controller tests
- `docker/` – database initialization scripts
- `docs/` – architecture diagrams and OpenAPI specification

## Requirements

- Java 21
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL (or the provided Docker setup)

## Local setup

### 1. Start PostgreSQL with Docker Compose

```bash
docker compose up -d postgres
```

This starts a PostgreSQL container with:
- database: `adoptiondb`
- user: `adoption`
- password: `adoption123`

### 2. Build and run the application

```bash
mvn clean spring-boot:run
```

The service will start on `http://localhost:8080`.

## Default credentials

The application uses basic auth for protected endpoints.

- username: `admin`
- password: `admin123`

## API documentation

The service exposes OpenAPI documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

A static specification is also available in [docs/openapi.yaml](docs/openapi.yaml).

## Main endpoints

### Pets

- `GET /pets` – list available pets
- `GET /pets/{petId}` – get pet details
- `GET /pets/{petId}/pictures` – list picture IDs for a pet
- `GET /pets/{petId}/pictures/{pictureId}` – retrieve a picture by ID
- `POST /pets` – create a pet (organization member only)
- `PATCH /pets/{petId}` – partially update pet information (organization member only)

The PATCH payload uses optional fields. When the `pictures` array is included, the service compares it with the existing picture set and keeps matching pictures, removes ones that are no longer present, and adds new ones.

### Applications

- `POST /applications` – submit an adoption application
- `PATCH /applications/{applicationId}` – review or update an application (organization member only)

### Example: create a pet with an image

The `pictures` field accepts base64-encoded image bytes. A valid example request is:

```bash
curl -u admin:admin123 -X POST http://localhost:8080/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Milo",
    "species": "Cat",
    "breed": "Siamese",
    "ageMonths": 12,
    "description": "Friendly cat",
    "status": "AVAILABLE",
    "pictures": ["iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAIAAG4hWK5AAAAAElFTkSuQmCC"]
  }'
```

The `pictures` array can contain one or more image payloads.

## Database notes

The database initialization scripts under `docker/postgres/init/` create the required schema and migration logic for pet pictures.

The current implementation uses a one-to-many relationship between `pets` and `pet_pictures`, with a database trigger enforcing that each pet keeps at least one picture.

## Testing

Run the test suite with:

```bash
mvn test
```

## Documentation

Additional documentation is available in:

- [docs/architecture/sequence-models.md](docs/architecture/sequence-models.md)
- [docs/openapi.yaml](docs/openapi.yaml)
