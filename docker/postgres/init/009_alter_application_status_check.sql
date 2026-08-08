-- Migration: extend applications.status CHECK constraint to include COMPLETED
ALTER TABLE applications DROP CONSTRAINT IF EXISTS applications_status_check;
ALTER TABLE applications ADD CONSTRAINT applications_status_check
    CHECK (status IN ('PENDING', 'NEEDS_INFO', 'APPROVED', 'REJECTED', 'COMPLETED'));
