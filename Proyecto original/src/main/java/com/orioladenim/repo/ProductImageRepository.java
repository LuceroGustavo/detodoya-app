package com.orioladenim.repo;

import com.orioladenim.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    // Buscar imágenes por producto
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.pId = :productId ORDER BY pi.displayOrder ASC")
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(@Param("productId") Integer productId);
    
    // Buscar imagen principal de un producto
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.pId = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Integer productId);
    
    // Contar imágenes de un producto
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.pId = :productId")
    Long countByProductId(@Param("productId") Integer productId);
    
    // Buscar imágenes por ruta
    Optional<ProductImage> findByImagePath(String imagePath);
    
    // Eliminar imágenes de un producto
    @Query("DELETE FROM ProductImage pi WHERE pi.product.pId = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}

