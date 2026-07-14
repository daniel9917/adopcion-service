package com.example.adoption.domain;

public enum Breed {
    SIAMESE(Species.FELINE),
    PERSIAN(Species.FELINE),
    MAINE_COON(Species.FELINE),
    RAGDOLL(Species.FELINE),
    BENGAL(Species.FELINE),
    BRITISH_SHORTHAIR(Species.FELINE),
    ABYSSINIAN(Species.FELINE),
    RUSSIAN_BLUE(Species.FELINE),

    LABRADOR(Species.CANINE),
    GERMAN_SHEPHERD(Species.CANINE),
    POODLE(Species.CANINE),
    BULLDOG(Species.CANINE),
    BEAGLE(Species.CANINE),
    GOLDEN_RETRIEVER(Species.CANINE),
    FRENCH_BULLDOG(Species.CANINE),

    MIXED(Species.FELINE);

    private final Species species;

    Breed(Species species) {
        this.species = species;
    }

    public Species getSpecies() {
        return species;
    }
}
