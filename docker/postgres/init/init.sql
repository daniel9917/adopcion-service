CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'ORG_MEMBER')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    age_months INTEGER,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE', 'PENDING', 'ADOPTED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applications (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE RESTRICT,
    applicant_name VARCHAR(100) NOT NULL,
    applicant_email VARCHAR(255) NOT NULL,
    applicant_phone VARCHAR(30) NOT NULL,
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'NEEDS_INFO', 'APPROVED', 'REJECTED')),
    review_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pets_status ON pets(status);
CREATE INDEX IF NOT EXISTS idx_applications_pet_id ON applications(pet_id);
CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(status);

INSERT INTO users (email, password_hash, role)
VALUES
    ('org@example.com', '$2a$10$placeholder.hash', 'ORG_MEMBER');

INSERT INTO pets (name, species, breed, age_months, description, status)
VALUES
    ('Luna', 'Dog', 'Labrador', 24, 'Friendly and playful dog looking for a forever home.', 'AVAILABLE'),
    ('Milo', 'Cat', 'Siamese', 15, 'Calm and affectionate cat that enjoys quiet spaces.', 'AVAILABLE'),
    ('Buddy', 'Dog', 'Mixed Breed', 36, 'Energetic dog that loves outdoor walks.', 'PENDING');

INSERT INTO applications (pet_id, applicant_name, applicant_email, applicant_phone, message, status)
VALUES
    (1, 'Ana Gomez', 'ana@example.com', '+54 11 5555-1234', 'I would love to adopt Luna.', 'PENDING');