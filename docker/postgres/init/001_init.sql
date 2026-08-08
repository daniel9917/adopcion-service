CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('REGULAR', 'ORGANIZATION')),
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    sex VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN'
        CHECK (sex IN ('MALE', 'FEMALE', 'UNKNOWN')),
    age_months INTEGER,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE', 'PENDING', 'ADOPTED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_pictures (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    data BYTEA NOT NULL,
    content_type VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pet_special_conditions (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    condition VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applications (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE RESTRICT,
    user_id BIGINT NOT NULL REFERENCES users(id),
    applicant_name VARCHAR(100) NOT NULL,
    applicant_email VARCHAR(255) NOT NULL,
    applicant_phone VARCHAR(30) NOT NULL,
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'NEEDS_INFO', 'APPROVED', 'REJECTED', 'COMPLETED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS adoption_application_review_notes (
    id BIGSERIAL PRIMARY KEY,
    adoption_application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    note VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pets_status ON pets(status);
CREATE INDEX IF NOT EXISTS idx_applications_user_id ON applications(user_id);
CREATE INDEX IF NOT EXISTS idx_applications_pet_id ON applications(pet_id);
CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(status);
CREATE INDEX IF NOT EXISTS idx_review_notes_application_id ON adoption_application_review_notes(adoption_application_id);
CREATE INDEX IF NOT EXISTS idx_review_notes_user_id ON adoption_application_review_notes(user_id);

INSERT INTO users (user_type, name, last_name, email, password, city, phone_number)
VALUES
    ('ORGANIZATION', 'Ana', 'Gomez', 'org@example.com', '$2a$10$0EUhQosrKbTutfjLCdP2z.gY9nenjkWexbGUYI/bcDlslzR8BK.OG', 'Bogota', '+57 1 5555-1234'),
    ('REGULAR', 'Carlos', 'Lopez', 'carlos@example.com', '$2a$10$0EUhQosrKbTutfjLCdP2z.gY9nenjkWexbGUYI/bcDlslzR8BK.OG', 'Medellin', '+57 4 5555-5678');

INSERT INTO pets (name, species, breed, sex, age_months, description, status)
VALUES
    ('Luna', 'CANINE', 'LABRADOR', 'FEMALE', 24, 'Friendly and playful dog looking for a forever home.', 'AVAILABLE'),
    ('Milo', 'FELINE', 'SIAMESE', 'MALE', 15, 'Calm and affectionate cat that enjoys quiet spaces.', 'AVAILABLE'),
    ('Buddy', 'CANINE', 'MIXED', 'MALE', 36, 'Energetic dog that loves outdoor walks.', 'PENDING');

INSERT INTO applications (pet_id, user_id, applicant_name, applicant_email, applicant_phone, message, status)
VALUES
    (1, 2, 'Ana Gomez', 'ana@example.com', '+54 11 5555-1234', 'I would love to adopt Luna.', 'PENDING');