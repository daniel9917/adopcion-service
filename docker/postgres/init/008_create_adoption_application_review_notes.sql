-- Migration: create adoption_application_review_notes table and remove review_notes from applications

CREATE TABLE IF NOT EXISTS adoption_application_review_notes (
    id BIGSERIAL PRIMARY KEY,
    adoption_application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    note VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_review_notes_application_id ON adoption_application_review_notes(adoption_application_id);
CREATE INDEX IF NOT EXISTS idx_review_notes_user_id ON adoption_application_review_notes(user_id);

ALTER TABLE applications DROP COLUMN IF EXISTS review_notes;
