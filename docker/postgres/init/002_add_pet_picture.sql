-- Migration: create pet_pictures table for storing pet images
CREATE TABLE IF NOT EXISTS pet_pictures (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    data BYTEA NOT NULL,
    content_type VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION ensure_pet_has_picture()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        PERFORM 1 FROM pet_pictures WHERE pet_id = OLD.pet_id AND id <> OLD.id LIMIT 1;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'Pet % must have at least one picture', OLD.pet_id;
        END IF;
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' AND OLD.pet_id IS DISTINCT FROM NEW.pet_id THEN
        PERFORM 1 FROM pet_pictures WHERE pet_id = OLD.pet_id AND id <> OLD.id LIMIT 1;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'Pet % must have at least one picture', OLD.pet_id;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER pet_pictures_ensure_has_one_picture
BEFORE DELETE OR UPDATE OF pet_id ON pet_pictures
FOR EACH ROW
EXECUTE FUNCTION ensure_pet_has_picture();
