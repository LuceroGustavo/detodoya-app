package com.detodoya.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Color para gestionar colores normalizados de productos
 * Permite al administrador crear, editar y gestionar colores personalizados
 */
@Entity
@Table(name = "colors")
public class Color {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del color es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(name = "description", length = 200)
    private String description;
    
    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "El código hexadecimal debe tener formato #RRGGBB o estar vacío")
    @Column(name = "hex_code", length = 7, nullable = true, columnDefinition = "VARCHAR(7) NULL")
    private String hexCode; // Código hexadecimal del color (#ff6b6b) - Opcional para patrones
    
    @Column(name = "image_path", length = 500, nullable = true)
    private String imagePath; // Ruta de la imagen del patrón (ej: "colors/animal_print.webp") - Opcional, alternativa a hexCode
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false; // Indica si es un color predeterminado del sistema
    
    @Column(name = "display_order")
    private Integer displayOrder = 0; // Orden de visualización en el catálogo
    
    @Column(name = "product_count")
    private Integer productCount = 0; // Contador de productos con este color
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relación con Product (One-to-Many)
    // NO usar CascadeType.ALL para evitar eliminar productos al eliminar color
    @OneToMany(mappedBy = "colorEntity", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    // Constructores
    public Color() {}
    
    public Color(String name, String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
        this.isActive = true;
        this.isDefault = false;
        this.displayOrder = 0;
        this.productCount = 0;
    }
    
    public Color(String name, String description, String hexCode) {
        this.name = name;
        this.description = description;
        this.hexCode = hexCode;
        this.isActive = true;
        this.isDefault = false;
        this.displayOrder = 0;
        this.productCount = 0;
    }
    
    // Métodos de utilidad
    public void incrementProductCount() {
        this.productCount = (this.productCount == null) ? 1 : this.productCount + 1;
    }
    
    public void decrementProductCount() {
        if (this.productCount != null && this.productCount > 0) {
            this.productCount--;
        }
    }
    
    public boolean hasProducts() {
        return this.productCount != null && this.productCount > 0;
    }
    
    public String getDisplayName() {
        return this.name != null ? this.name : "Sin color";
    }
    
    public String getHexCodeOrDefault() {
        return this.hexCode != null ? this.hexCode : "#6c757d";
    }
    
    public String getRgbCode() {
        if (hexCode == null || !hexCode.startsWith("#")) {
            return "rgb(108, 117, 125)"; // Gris por defecto
        }
        
        try {
            String hex = hexCode.substring(1);
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return String.format("rgb(%d, %d, %d)", r, g, b);
        } catch (Exception e) {
            return "rgb(108, 117, 125)";
        }
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHexCode() {
        return hexCode;
    }
    
    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    /**
     * Verifica si el color tiene una imagen de patrón
     */
    public boolean hasPatternImage() {
        return imagePath != null && !imagePath.trim().isEmpty();
    }
    
    /**
     * Obtiene la URL completa de la imagen del patrón
     */
    public String getImageUrl() {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        return "/uploads/" + imagePath;
    }
    
    /**
     * Obtiene la URL del thumbnail de la imagen del patrón
     */
    public String getThumbnailUrl() {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        return "/uploads/thumbnails/colors/" + imagePath.substring(imagePath.lastIndexOf('/') + 1);
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public Integer getProductCount() {
        return productCount;
    }
    
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    @Override
    public String toString() {
        return "Color{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hexCode='" + hexCode + '\'' +
                ", isActive=" + isActive +
                ", displayOrder=" + displayOrder +
                ", productCount=" + productCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return id != null && id.equals(color.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
