-- Migration: create pet_special_conditions table for storing pet conditions

CREATE TABLE IF NOT EXISTS pet_special_conditions (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    condition VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);