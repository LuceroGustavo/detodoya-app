package com.detodoya.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para gestionar productos favoritos de usuarios
 * Soporta tanto usuarios autenticados como sesiones anónimas
 */
@Entity
@Table(name = "favorites", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class Favorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Usuario autenticado (puede ser null para usuarios anónimos)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    
    /**
     * Identificador de sesión para usuarios no autenticados
     */
    @Column(name = "session_id", length = 100, nullable = true)
    private String sessionId;
    
    /**
     * Producto favorito
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructores
    public Favorite() {}
    
    public Favorite(User user, Product product) {
        this.user = user;
        this.product = product;
    }
    
    public Favorite(String sessionId, Product product) {
        this.sessionId = sessionId;
        this.product = product;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Verifica si el favorito pertenece a un usuario autenticado
     */
    public boolean isAuthenticatedUser() {
        return user != null;
    }
    
    /**
     * Verifica si el favorito pertenece a una sesión anónima
     */
    public boolean isAnonymousSession() {
        return sessionId != null && user == null;
    }
}

