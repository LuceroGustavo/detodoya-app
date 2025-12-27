package com.detodoya.enums;

/**
 * Enum que representa los diferentes tipos de productos que se pueden publicar en Detodoya.com
 * Permite mostrar campos específicos según el tipo de producto seleccionado
 */
public enum TipoProducto {
    INDUMENTARIA("Indumentaria"),
    ELECTRONICA("Electrónica"),
    HOGAR("Hogar y Muebles"),
    DEPORTES("Deportes y Fitness"),
    JUGUETES("Juguetes"),
    LIBROS("Libros"),
    BELLEZA("Belleza y Cuidado Personal"),
    AUTOMOTOR("Automotor"),
    OTROS("Otros");
    
    private final String displayName;
    
    TipoProducto(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Determina si este tipo de producto requiere campos específicos de indumentaria
     * (talles, géneros, temporadas)
     */
    public boolean requiereCamposIndumentaria() {
        return this == INDUMENTARIA;
    }
    
    /**
     * Determina si este tipo de producto requiere campos específicos de electrónica
     * (marca, modelo, garantía, especificaciones técnicas)
     */
    public boolean requiereCamposElectronica() {
        return this == ELECTRONICA;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

