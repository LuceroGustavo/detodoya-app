package com.detodoya.repo;

import com.detodoya.entity.Category;
import com.detodoya.entity.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de subcategorías
 */
@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    
    /**
     * Buscar subcategorías activas ordenadas por displayOrder
     */
    @Query("SELECT s FROM Subcategoria s WHERE s.isActive = true ORDER BY s.displayOrder ASC, s.name ASC")
    List<Subcategoria> findActiveSubcategoriasOrdered();
    
    /**
     * Buscar subcategorías por categoría
     */
    @Query("SELECT s FROM Subcategoria s WHERE s.category = :category AND s.isActive = true ORDER BY s.displayOrder ASC, s.name ASC")
    List<Subcategoria> findByCategory(@Param("category") Category category);
    
    /**
     * Buscar subcategorías activas por ID de categoría
     */
    @Query("SELECT s FROM Subcategoria s WHERE s.category.id = :categoryId AND s.isActive = true ORDER BY s.displayOrder ASC, s.name ASC")
    List<Subcategoria> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * Buscar subcategoría por nombre (case insensitive)
     */
    @Query("SELECT s FROM Subcategoria s WHERE LOWER(s.name) = LOWER(:name)")
    Optional<Subcategoria> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Verificar si existe una subcategoría con el mismo nombre en la misma categoría (excluyendo la actual)
     */
    @Query("SELECT COUNT(s) > 0 FROM Subcategoria s WHERE LOWER(s.name) = LOWER(:name) AND s.category.id = :categoryId AND s.id != :id")
    boolean existsByNameIgnoreCaseAndCategoryIdAndIdNot(@Param("name") String name, @Param("categoryId") Long categoryId, @Param("id") Long id);
    
    /**
     * Buscar subcategorías que contengan el texto en el nombre o descripción
     */
    @Query("SELECT s FROM Subcategoria s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND s.isActive = true " +
           "ORDER BY s.displayOrder ASC, s.name ASC")
    List<Subcategoria> searchActiveSubcategorias(@Param("search") String search);
    
    /**
     * Obtener el siguiente número de orden disponible para una categoría
     */
    @Query("SELECT COALESCE(MAX(s.displayOrder), 0) + 1 FROM Subcategoria s WHERE s.category.id = :categoryId")
    Integer getNextDisplayOrderForCategory(@Param("categoryId") Long categoryId);
    
    /**
     * Contar productos por subcategoría
     */
    @Query("SELECT s.id, s.name, COUNT(p) as productCount " +
           "FROM Subcategoria s LEFT JOIN s.products p " +
           "WHERE s.isActive = true " +
           "GROUP BY s.id, s.name " +
           "ORDER BY productCount DESC, s.name ASC")
    List<Object[]> countProductsBySubcategoria();
    
    /**
     * Buscar subcategorías sin productos
     */
    @Query("SELECT s FROM Subcategoria s " +
           "LEFT JOIN s.products p " +
           "WHERE s.isActive = true AND p IS NULL " +
           "ORDER BY s.displayOrder ASC, s.name ASC")
    List<Subcategoria> findEmptySubcategorias();
}

