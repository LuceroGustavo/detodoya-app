package com.detodoya.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Subcategoria para gestionar subcategorías dentro de categorías principales
 * Permite una estructura jerárquica: Categoría -> Subcategorías -> Productos
 */
@Entity
@Table(name = "subcategorias")
public class Subcategoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;
    
    // Relación Many-to-One con Category (cada subcategoría pertenece a una categoría)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0; // Orden de visualización dentro de la categoría
    
    @Column(name = "product_count")
    private Integer productCount = 0; // Contador de productos en esta subcategoría
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relación Many-to-Many con Product (lado inverso)
    @ManyToMany(mappedBy = "subcategorias", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    // Constructores
    public Subcategoria() {}
    
    public Subcategoria(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
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
        return this.name != null ? this.name : "Sin subcategoría";
    }
    
    public String getFullPath() {
        return (category != null ? category.getName() + " > " : "") + this.name;
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
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
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
        return "Subcategoria{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category=" + (category != null ? category.getName() : "null") +
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
        Subcategoria that = (Subcategoria) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

