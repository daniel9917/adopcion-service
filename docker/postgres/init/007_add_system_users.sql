-- Migration: link applications to users and ensure the seed system users exist
-- Idempotent so it can safely run on a fresh database (base schema already in 001_init.sql)
-- or on an existing database initialized before the users/applications relationship existed.

ALTER TABLE applications ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id);

INSERT INTO users (user_type, name, last_name, email, password, city, phone_number)
VALUES
    ('ORGANIZATION', 'Ana', 'Gomez', 'org@example.com', '$2a$10$0EUhQosrKbTutfjLCdP2z.gY9nenjkWexbGUYI/bcDlslzR8BK.OG', 'Bogota', '+57 1 5555-1234'),
    ('REGULAR', 'Carlos', 'Lopez', 'carlos@example.com', '$2a$10$0EUhQosrKbTutfjLCdP2z.gY9nenjkWexbGUYI/bcDlslzR8BK.OG', 'Medellin', '+57 4 5555-5678')
ON CONFLICT (email) DO NOTHING;

CREATE INDEX IF NOT EXISTS idx_applications_user_id ON applications(user_id);
