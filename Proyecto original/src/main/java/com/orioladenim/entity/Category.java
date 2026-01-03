package com.orioladenim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Category para gestionar categorías dinámicas de productos
 * Permite al administrador crear, editar y gestionar categorías personalizadas
 */
@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "image_path", length = 500)
    private String imagePath; // Ruta de la imagen de la categoría
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0; // Orden de visualización en el catálogo
    
    @Column(name = "product_count")
    private Integer productCount = 0; // Contador de productos en esta categoría
    
    @Column(name = "show_in_carousel")
    private Boolean showInCarousel = false; // Si aparece en el carrusel principal
    
    @Column(name = "carousel_order")
    private Integer carouselOrder = 0; // Orden de aparición en el carrusel
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relación con Product (Many-to-Many) - lado inverso
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    // Campo para compatibilidad con backups del servidor Node
    @Transient
    private List<Long> productIds = new ArrayList<>();
    
    
    // Constructores
    public Category() {}
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
        this.displayOrder = 0;
        this.productCount = 0;
    }
    
    public Category(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.isActive = true;
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
        return this.name != null ? this.name : "Sin categoría";
    }
    
    public String getImagePathOrDefault() {
        return this.imagePath != null ? this.imagePath : "/img/categories/default-category.jpg";
    }
    
    /**
     * Verifica si la categoría está lista para aparecer en el carrusel
     * (debe estar marcada para carrusel Y tener imagen)
     */
    public boolean isReadyForCarousel() {
        return Boolean.TRUE.equals(this.showInCarousel) && 
               this.imagePath != null && 
               !this.imagePath.trim().isEmpty();
    }
    
    // Métodos para manejar la relación Many-to-Many
    public void agregarProducto(Product producto) {
        if (!products.contains(producto)) {
            products.add(producto);
            incrementProductCount();
        }
    }
    
    public void removerProducto(Product producto) {
        if (products.remove(producto)) {
            decrementProductCount();
        }
    }
    
    public boolean tieneProducto(Product producto) {
        return products.contains(producto);
    }
    
    // Método para obtener el total de productos
    public int getTotalProductos() {
        return (products != null ? products.size() : 0);
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
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public Boolean getShowInCarousel() {
        return showInCarousel;
    }
    
    public void setShowInCarousel(Boolean showInCarousel) {
        this.showInCarousel = showInCarousel;
    }
    
    public Integer getCarouselOrder() {
        return carouselOrder;
    }
    
    public void setCarouselOrder(Integer carouselOrder) {
        this.carouselOrder = carouselOrder;
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
    
    public List<Long> getProductIds() {
        return productIds;
    }
    
    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
    
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", isActive=" + isActive +
                ", displayOrder=" + displayOrder +
                ", productCount=" + productCount +
                ", showInCarousel=" + showInCarousel +
                ", carouselOrder=" + carouselOrder +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
