package com.orioladenim.repo;

import com.orioladenim.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    
    // Buscar productos activos
    List<Product> findByActivoTrue();
    
    // Buscar productos destacados y activos
    List<Product> findByEsDestacadoTrueAndActivoTrue();
    
    // Buscar productos nuevos y activos
    List<Product> findByEsNuevoTrueAndActivoTrue();
    
    // Buscar productos por género (usando la nueva relación Many-to-Many)
    @Query("SELECT p FROM Product p JOIN p.generos g WHERE g = :genero AND p.activo = true")
    List<Product> findByGeneroAndActivoTrue(@Param("genero") String genero);
    
    // Buscar productos por temporada (usando la nueva relación Many-to-Many)
    @Query("SELECT p FROM Product p JOIN p.temporadas t WHERE t = :temporada AND p.activo = true")
    List<Product> findByTemporadaAndActivoTrue(@Param("temporada") String temporada);
    
    // Buscar productos con descuento
    @Query("SELECT p FROM Product p WHERE p.descuentoPorcentaje > 0 AND p.activo = true")
    List<Product> findProductosConDescuento();
    
    // Buscar productos por rango de precio
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :precioMin AND :precioMax AND p.activo = true")
    List<Product> findByPrecioBetween(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax);
    
    // Buscar productos por nombre (búsqueda parcial)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Product> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
    
    // Buscar productos por color
    @Query("SELECT p FROM Product p WHERE LOWER(p.color) LIKE LOWER(CONCAT('%', :color, '%')) AND p.activo = true")
    List<Product> findByColorContainingIgnoreCase(@Param("color") String color);
    
    // Contar productos por categoría (usando Many-to-Many)
    @Query("SELECT COUNT(p) FROM Product p JOIN p.categories c WHERE c.name = :categoria AND p.activo = true")
    Long countByCategoria(@Param("categoria") String categoria);
    
    // Obtener productos más recientes
    @Query("SELECT p FROM Product p WHERE p.activo = true ORDER BY p.fechaCreacion DESC")
    List<Product> findProductosRecientes();
    
    // Buscar productos por nombre (sin filtro de activo para admin)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Buscar productos por categoría (sin filtro de activo para admin)
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoria, '%'))")
    List<Product> findByCategoriesNameContainingIgnoreCase(@Param("categoria") String categoria);
    
    // Obtener producto con colores (primera consulta)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.colores WHERE p.pId = :id")
    Optional<Product> findByIdWithColors(@Param("id") Integer id);
    
    // Obtener producto con categorías
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.pId = :id")
    Optional<Product> findByIdWithCategories(@Param("id") Integer id);
    
    // Obtener producto con imágenes
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.pId = :id")
    Optional<Product> findByIdWithImages(@Param("id") Integer id);
}

