# Adoption Service

Adoption Service is a Spring Boot 3 application for managing pets, pet pictures, and adoption applications for an animal rescue organization.

## Features

- Manage pets with status, attributes, and multiple stored pictures
- Store pet pictures as binary data in PostgreSQL
- Support full pet replacement updates and partial patch updates with picture-list merging
- Submit and review adoption applications
- JWT authentication with role-based access control (REGULAR vs ORGANIZATION users)
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

## Authentication

The service issues stateless JWT bearer tokens.

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "secret123"
  }'
```

A successful login returns a JSON body with a `token` field plus user information.

### Using the token

Protected endpoints accept the token as a Bearer header:

```bash
curl http://localhost:8080/pets \
  -H "Authorization: Bearer <token>"
```

### JWT configuration

- `app.jwt.secret` – signing key (must be changed in production)
- `app.jwt.expiration-ms` – token lifetime in milliseconds (default 24h)

## API documentation

The service exposes OpenAPI documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

A static specification is also available in [docs/openapi.yaml](docs/openapi.yaml).

## Access matrix

| Endpoint | Access |
| --- | --- |
| `POST /auth/login` | Public |
| `POST /users` | Public |
| `GET /health` | Public |
| `GET /pets` | Public |
| `GET /pets/{petId}` | Public |
| `GET /pets/{petId}/pictures` | Public |
| `GET /pets/{petId}/pictures/{pictureId}` | Public |
| `POST /pets` | `ORGANIZATION` only |
| `PATCH /pets/{petId}` | `ORGANIZATION` only |
| `POST /applications` | Any authenticated user (applicant is the token user) |
| `GET /applications` | Authenticated (org sees all, regular sees own); paginated |
| `GET /applications/{applicationId}` | Authenticated (org any, regular own only) |
| `PATCH /applications/{applicationId}` | Authenticated; partial update. Status required; transitions constrained by role policy. REGULAR owners may patch only their own applications (NEEDS_INFO→PENDING). `ORGANIZATION` may patch any (PENDING→NEEDS_INFO/APPROVED/REJECTED, APPROVED→REJECTED/COMPLETED). `COMPLETED` marks the pet `ADOPTED`. |
| `GET /applications/{applicationId}/review-notes` | Authenticated |
| `POST /applications/{applicationId}/review-notes` | Authenticated owner of the application when status is `PENDING` or `NEEDS_INFO`, or `ORGANIZATION` at any time |

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

- `POST /applications` – submit an adoption application (the authenticated user is the applicant)
- `GET /applications` – list applications (organization users see all, regular users see only their own). Paginated via `?page=0&size=20&sort=createdAt,desc`; the response is a Spring Data `Page` (fields `content`, `totalElements`, `totalPages`, `number`, `size`).
- `GET /applications/{applicationId}` – get an application (organization users any, regular users only their own)
- `PATCH /applications/{applicationId}` – partially update an application. The payload always requires a `status`; `applicantName`, `applicantEmail`, `applicantPhone`, and `message` are optional and only present fields are updated. Status transitions are constrained by role: regular users may only move their own application from `NEEDS_INFO` back to `PENDING`; organization users may move `PENDING` to `NEEDS_INFO`/`APPROVED`/`REJECTED` and `APPROVED` to `REJECTED`/`COMPLETED`. A disallowed transition returns `409 Conflict`. Marking an application `COMPLETED` also marks the associated pet as `ADOPTED`.
- `GET /applications/{applicationId}/review-notes` – list the review notes for an application (any authenticated user)
- `POST /applications/{applicationId}/review-notes` – add a review note. Organization users can add notes to any application at any time; regular users can only add notes to applications they own while the status is `PENDING` or `NEEDS_INFO`.

### Example: create a pet with an image

The `pictures` field accepts base64-encoded image bytes. A valid example request is:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@org.com","password":"orgpass456"}' | jq -r '.token')

curl -X POST http://localhost:8080/pets \
  -H "Authorization: Bearer $TOKEN" \
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
