# Sequence models for the adoption service

This document describes the main request flows for the initial adoption service endpoints.

## 1. List available pets

Endpoint: GET /pets

```mermaid
sequenceDiagram
    actor Client
    participant Controller as PetController
    participant Service as PetService
    participant Repo as PetRepository
    participant DB as Database

    Client->>Controller: GET /pets
    Controller->>Service: getAvailablePets()
    Service->>Repo: findByStatus(AVAILABLE)
    Repo->>DB: SELECT * FROM pets WHERE status = 'AVAILABLE'
    DB-->>Repo: Pet rows
    Repo-->>Service: List<Pet>
    Service-->>Controller: List<Pet>
    Controller-->>Client: 200 OK + JSON list
```

## 2. Get pet details

Endpoint: GET /pets/{petId}

```mermaid
sequenceDiagram
    actor Client
    participant Controller as PetController
    participant Service as PetService
    participant Repo as PetRepository
    participant DB as Database

    Client->>Controller: GET /pets/{petId}
    Controller->>Service: getPet(petId)
    Service->>Repo: findById(petId)
    Repo->>DB: SELECT * FROM pets WHERE id = ?
    DB-->>Repo: Pet row or null
    Repo-->>Service: Optional<Pet>
    Service-->>Controller: Pet or error
    Controller-->>Client: 200 OK + pet JSON / 404 Not Found
```

## 3. Create a new pet (organization)

Endpoint: POST /pets

```mermaid
sequenceDiagram
    actor OrgMember as Organization Member
    participant Controller as PetController
    participant Service as PetService
    participant Repo as PetRepository
    participant DB as Database

    OrgMember->>Controller: POST /pets with pet payload
    Controller->>Service: createPet(request)
    Service->>Repo: save(pet)
    Repo->>DB: INSERT INTO pets (...)
    DB-->>Repo: inserted row
    Repo-->>Service: Pet
    Service-->>Controller: Pet
    Controller-->>OrgMember: 201 Created + pet JSON
```

## 4. Update a pet (organization)

Endpoint: PATCH /pets/{petId}

```mermaid
sequenceDiagram
    actor OrgMember as Organization Member
    participant Controller as PetController
    participant Service as PetService
    participant Repo as PetRepository
    participant DB as Database

    OrgMember->>Controller: PATCH /pets/{petId} with changes
    Controller->>Service: updatePet(petId, request)
    Service->>Repo: findById(petId)
    Repo->>DB: SELECT * FROM pets WHERE id = ?
    DB-->>Repo: Pet row
    Service->>Repo: save(updatedPet)
    Repo->>DB: UPDATE pets SET ...
    DB-->>Repo: updated row
    Repo-->>Service: Pet
    Service-->>Controller: Pet
    Controller-->>OrgMember: 200 OK + updated pet JSON
```

## 5. Submit an adoption application

Endpoint: POST /applications

```mermaid
sequenceDiagram
    actor Client
    participant Controller as ApplicationController
    participant Service as ApplicationService
    participant PetRepo as PetRepository
    participant AppRepo as ApplicationRepository
    participant DB as Database

    Client->>Controller: POST /applications with applicant data
    Controller->>Service: createApplication(request)
    Service->>PetRepo: findById(request.petId)
    PetRepo->>DB: SELECT * FROM pets WHERE id = ?
    DB-->>PetRepo: Pet row
    Service->>Service: validate pet is AVAILABLE
    Service->>AppRepo: save(application)
    AppRepo->>DB: INSERT INTO applications (...)
    DB-->>AppRepo: inserted row
    AppRepo-->>Service: AdoptionApplication
    Service-->>Controller: AdoptionApplication
    Controller-->>Client: 201 Created + application JSON
```

## 6. Review or update an application (organization)

Endpoint: PATCH /applications/{applicationId}

```mermaid
sequenceDiagram
    actor OrgMember as Organization Member
    participant Controller as ApplicationController
    participant Service as ApplicationService
    participant Repo as ApplicationRepository
    participant DB as Database

    OrgMember->>Controller: PATCH /applications/{applicationId} with status/review notes
    Controller->>Service: updateApplication(applicationId, request)
    Service->>Repo: findById(applicationId)
    Repo->>DB: SELECT * FROM applications WHERE id = ?
    DB-->>Repo: Application row
    Service->>Repo: save(updatedApplication)
    Repo->>DB: UPDATE applications SET status = ?, review_notes = ?
    DB-->>Repo: updated row
    Repo-->>Service: AdoptionApplication
    Service-->>Controller: AdoptionApplication
    Controller-->>OrgMember: 200 OK + application JSON
```
