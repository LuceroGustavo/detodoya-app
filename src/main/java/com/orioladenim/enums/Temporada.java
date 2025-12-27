package com.orioladenim.enums;

public enum Temporada {
    VERANO("Verano"),
    INVIERNO("Invierno"),
    PRIMAVERA("Primavera"),
    OTONO("Otoño"),
    TODO_EL_ANO("Todo el año");

    private final String displayName;

    Temporada(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

