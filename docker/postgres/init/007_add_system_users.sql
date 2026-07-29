DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
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

ALTER TABLE applications ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id);

INSERT INTO users (user_type, name, last_name, email, password, city, phone_number)
VALUES
    ('ORGANIZATION', 'Ana', 'Gomez', 'org@example.com', '$2a$10$0EUhQosrKbTutfjLCdP2z.gY9nenjkWexbGUYI/bcDlslzR8BK.OG', 'Bogota', '+57 1 5555-1234'),
    ('REGULAR', 'Carlos', 'Lopez', 'carlos@example.com', '$2a$10$0EUhQosrKbTutfjLCdP2z.gY9nenjkWexbGUYI/bcDlslzR8BK.OG', 'Medellin', '+57 4 5555-5678');

CREATE INDEX IF NOT EXISTS idx_applications_user_id ON applications(user_id);
