-- Migration: convert species and breed columns from free-text to enum values
UPDATE pets SET species = 'CANINE' WHERE species = 'Dog';
UPDATE pets SET species = 'FELINE' WHERE species = 'Cat';
UPDATE pets SET breed = 'LABRADOR' WHERE breed = 'Labrador';
UPDATE pets SET breed = 'SIAMESE' WHERE breed = 'Siamese';
UPDATE pets SET breed = 'MIXED' WHERE breed = 'Mixed Breed';

ALTER TABLE pets ADD CONSTRAINT chk_species
    CHECK (species IN ('FELINE', 'CANINE'));
ALTER TABLE pets ADD CONSTRAINT chk_breed
    CHECK (breed IN ('SIAMESE','PERSIAN','MAINE_COON','RAGDOLL','BENGAL',
                     'BRITISH_SHORTHAIR','ABYSSINIAN','RUSSIAN_BLUE',
                     'LABRADOR','GERMAN_SHEPHERD','POODLE','BULLDOG',
                     'BEAGLE','GOLDEN_RETRIEVER','FRENCH_BULLDOG','MIXED'));
