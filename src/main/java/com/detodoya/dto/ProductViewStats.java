package com.detodoya.dto;

import com.detodoya.entity.Product;
import java.math.BigDecimal;

/**
 * DTO para representar las estadísticas de vistas de un producto
 * Se usa en las respuestas de la API de analytics
 */
public class ProductViewStats {
    
    private Product product;
    private Long viewCount;
    
    // Constructores
    public ProductViewStats() {
    }
    
    public ProductViewStats(Product product, Long viewCount) {
        this.product = product;
        this.viewCount = viewCount;
    }
    
    // Getters y Setters
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    /**
     * Obtiene el nombre del producto
     */
    public String getProductName() {
        return product != null ? product.getName() : "Producto no encontrado";
    }
    
    /**
     * Obtiene la URL de la imagen principal del producto
     */
    public String getProductImage() {
        if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getImages().get(0).getImageUrl();
        }
        return "/images/placeholder.svg";
    }
    
    /**
     * Obtiene el precio del producto
     */
    public BigDecimal getProductPrice() {
        if (product != null && product.getPrice() != null) {
            return BigDecimal.valueOf(product.getPrice());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Obtiene el ID del producto
     */
    public Integer getProductId() {
        return product != null ? product.getPId() : null;
    }
    
    /**
     * Obtiene la descripción del producto (truncada)
     */
    public String getProductDescription() {
        if (product != null && product.getDescripcion() != null) {
            String desc = product.getDescripcion();
            return desc.length() > 100 ? desc.substring(0, 100) + "..." : desc;
        }
        return "Sin descripción";
    }
    
    /**
     * Obtiene las categorías del producto como string
     */
    public String getProductCategories() {
        if (product != null && product.getCategories() != null && !product.getCategories().isEmpty()) {
            return product.getCategories().stream()
                .map(category -> category.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin categoría");
        }
        return "Sin categoría";
    }
    
    /**
     * Obtiene el estado del producto
     */
    public String getProductStatus() {
        if (product != null) {
            return product.getActivo() ? "Activo" : "Inactivo";
        }
        return "Desconocido";
    }
}
