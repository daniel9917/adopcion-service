-- Migration: ensure pet_pictures.data is stored as bytea for PostgreSQL
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'pet_pictures'
          AND column_name = 'data'
          AND table_schema = 'public'
    ) THEN
        ALTER TABLE public.pet_pictures
            ALTER COLUMN data TYPE BYTEA USING data::bytea;
    END IF;
END $$;
