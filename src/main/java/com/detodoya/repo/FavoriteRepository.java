package com.detodoya.repo;

import com.detodoya.entity.Favorite;
import com.detodoya.entity.Product;
import com.detodoya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * Buscar favorito por usuario y producto
     */
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    
    /**
     * Buscar favorito por sesión y producto
     */
    Optional<Favorite> findBySessionIdAndProduct(String sessionId, Product product);
    
    /**
     * Buscar todos los favoritos de un usuario
     */
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Buscar todos los favoritos de una sesión
     */
    List<Favorite> findBySessionIdOrderByCreatedAtDesc(String sessionId);
    
    /**
     * Verificar si un producto es favorito de un usuario
     */
    boolean existsByUserAndProduct(User user, Product product);
    
    /**
     * Verificar si un producto es favorito de una sesión
     */
    boolean existsBySessionIdAndProduct(String sessionId, Product product);
    
    /**
     * Contar favoritos de un usuario
     */
    long countByUser(User user);
    
    /**
     * Contar favoritos de una sesión
     */
    long countBySessionId(String sessionId);
    
    /**
     * Eliminar favorito por usuario y producto
     */
    void deleteByUserAndProduct(User user, Product product);
    
    /**
     * Eliminar favorito por sesión y producto
     */
    void deleteBySessionIdAndProduct(String sessionId, Product product);
    
    /**
     * Obtener IDs de productos favoritos de un usuario
     */
    @Query("SELECT f.product.pId FROM Favorite f WHERE f.user = :user")
    List<Integer> findProductIdsByUser(@Param("user") User user);
    
    /**
     * Obtener IDs de productos favoritos de una sesión
     */
    @Query("SELECT f.product.pId FROM Favorite f WHERE f.sessionId = :sessionId")
    List<Integer> findProductIdsBySessionId(@Param("sessionId") String sessionId);
}

