package com.detodoya.repo;

import com.detodoya.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de categorías
 * Proporciona consultas personalizadas para operaciones de categorías
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Buscar categorías activas ordenadas por displayOrder
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findActiveCategoriesOrdered();
    
    /**
     * Buscar categoría por nombre (case insensitive)
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Verificar si existe una categoría con el mismo nombre (excluyendo la actual)
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    /**
     * Buscar categorías que contengan el texto en el nombre o descripción
     */
    @Query("SELECT c FROM Category c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND c.isActive = true " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> searchActiveCategories(@Param("search") String search);
    
    /**
     * Contar productos por categoría
     */
    @Query("SELECT c.id, c.name, COUNT(p) as productCount " +
           "FROM Category c LEFT JOIN c.products p " +
           "WHERE c.isActive = true " +
           "GROUP BY c.id, c.name " +
           "ORDER BY productCount DESC, c.name ASC")
    List<Object[]> countProductsByCategory();
    
    /**
     * Buscar categorías con productos
     */
    @Query("SELECT DISTINCT c FROM Category c " +
           "JOIN c.products p " +
           "WHERE c.isActive = true AND p.activo = true " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findCategoriesWithActiveProducts();
    
    /**
     * Buscar categorías con productos (versión alternativa usando subconsulta)
     */
    @Query("SELECT c FROM Category c " +
           "WHERE c.isActive = true " +
           "AND EXISTS (SELECT 1 FROM Product p JOIN p.categories pc WHERE pc = c AND p.activo = true) " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findCategoriesWithActiveProductsAlternative();
    
    /**
     * Buscar categorías con productos usando consulta nativa (más confiable)
     */
    @Query(value = "SELECT DISTINCT c.* FROM categories c " +
                   "INNER JOIN product_categories pc ON c.id = pc.category_id " +
                   "INNER JOIN product p ON pc.product_id = p.p_id " +
                   "WHERE c.is_active = true AND p.activo = true " +
                   "ORDER BY c.display_order ASC, c.name ASC", 
           nativeQuery = true)
    List<Category> findCategoriesWithActiveProductsNative();
    
    /**
     * Buscar categorías sin productos
     */
    @Query("SELECT c FROM Category c " +
           "LEFT JOIN c.products p " +
           "WHERE c.isActive = true AND p IS NULL " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findEmptyCategories();
    
    /**
     * Obtener el siguiente número de orden disponible
     */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) + 1 FROM Category c")
    Integer getNextDisplayOrder();
    
    /**
     * Buscar categorías por rango de orden
     */
    @Query("SELECT c FROM Category c " +
           "WHERE c.displayOrder BETWEEN :startOrder AND :endOrder " +
           "ORDER BY c.displayOrder ASC")
    List<Category> findByDisplayOrderRange(@Param("startOrder") Integer startOrder, 
                                         @Param("endOrder") Integer endOrder);
    
    /**
     * Actualizar contador de productos de una categoría
     */
    @Query("UPDATE Category c SET c.productCount = " +
           "(SELECT COUNT(p) FROM Product p JOIN p.categories cat WHERE cat = c AND p.activo = true) " +
           "WHERE c.id = :categoryId")
    void updateProductCount(@Param("categoryId") Long categoryId);
    
    /**
     * Buscar categorías por imagen
     */
    @Query("SELECT c FROM Category c WHERE c.imagePath = :imagePath AND c.isActive = true")
    List<Category> findByImagePath(@Param("imagePath") String imagePath);
    
    /**
     * Buscar categorías con imagen
     */
    @Query("SELECT c FROM Category c WHERE c.imagePath IS NOT NULL AND c.imagePath != '' AND c.isActive = true")
    List<Category> findCategoriesWithImages();
    
    /**
     * Buscar categorías listas para el carrusel (marcadas para carrusel Y con imagen)
     */
    @Query("SELECT c FROM Category c WHERE c.showInCarousel = true AND c.imagePath IS NOT NULL AND c.imagePath != '' AND c.isActive = true ORDER BY c.carouselOrder ASC, c.name ASC")
    List<Category> findReadyForCarousel();
    
    /**
     * Estadísticas de categorías
     */
    @Query("SELECT " +
           "COUNT(c) as totalCategories, " +
           "COUNT(CASE WHEN c.isActive = true THEN 1 END) as activeCategories, " +
           "COUNT(CASE WHEN c.isActive = false THEN 1 END) as inactiveCategories, " +
           "AVG(c.productCount) as averageProductsPerCategory, " +
           "MAX(c.productCount) as maxProductsInCategory " +
           "FROM Category c")
    Object[] getCategoryStatistics();
}
