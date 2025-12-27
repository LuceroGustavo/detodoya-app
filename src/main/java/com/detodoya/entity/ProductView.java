package com.detodoya.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad para trackear las visitas a productos
 * Se crea automáticamente una entrada cada vez que un usuario visita un producto
 */
@Entity
@Table(name = "product_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt = LocalDateTime.now();
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "referrer", length = 500)
    private String referrer;
    
    // Constructor para crear una nueva vista
    public ProductView(Product product, String ipAddress, String userAgent, String sessionId, String referrer) {
        this.product = product;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.referrer = referrer;
        this.viewedAt = LocalDateTime.now();
    }
}
