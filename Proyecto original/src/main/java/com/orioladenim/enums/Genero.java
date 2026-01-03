package com.orioladenim.enums;

public enum Genero {
    HOMBRE("Hombre"),
    MUJER("Mujer"),
    UNISEX("Unisex"),
    NINO("Niño"),
    NINA("Niña");

    private final String displayName;

    Genero(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

