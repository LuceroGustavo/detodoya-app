package com.orioladenim.repo;

import com.orioladenim.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión de colores
 * Proporciona consultas personalizadas para operaciones de colores
 */
@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    
    /**
     * Buscar colores activos ordenados por displayOrder
     */
    @Query("SELECT c FROM Color c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Color> findActiveColorsOrdered();
    
    /**
     * Buscar color por nombre (case insensitive)
     */
    @Query("SELECT c FROM Color c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Color> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Buscar color por código hexadecimal (case insensitive)
     */
    @Query("SELECT c FROM Color c WHERE LOWER(c.hexCode) = LOWER(:hexCode)")
    Optional<Color> findByHexCodeIgnoreCase(@Param("hexCode") String hexCode);
    
    /**
     * Verificar si existe un color con el mismo nombre (excluyendo el actual)
     */
    @Query("SELECT COUNT(c) > 0 FROM Color c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    /**
     * Verificar si existe un color con el mismo código hexadecimal (excluyendo el actual)
     */
    @Query("SELECT COUNT(c) > 0 FROM Color c WHERE LOWER(c.hexCode) = LOWER(:hexCode) AND c.id != :id")
    boolean existsByHexCodeIgnoreCaseAndIdNot(@Param("hexCode") String hexCode, @Param("id") Long id);
    
    /**
     * Buscar colores que contengan el texto en el nombre o descripción
     */
    @Query("SELECT c FROM Color c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND c.isActive = true " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Color> searchActiveColors(@Param("search") String search);
    
    /**
     * Contar productos por color
     */
    @Query("SELECT c.id, c.name, COUNT(p) as productCount " +
           "FROM Color c LEFT JOIN c.products p " +
           "WHERE c.isActive = true " +
           "GROUP BY c.id, c.name " +
           "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Object[]> countProductsByColor();
    
    /**
     * Obtener el siguiente orden de visualización
     */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) + 1 FROM Color c")
    Integer getNextDisplayOrder();
    
    /**
     * Buscar colores por rango de orden
     */
    @Query("SELECT c FROM Color c WHERE c.displayOrder BETWEEN :startOrder AND :endOrder ORDER BY c.displayOrder ASC")
    List<Color> findByDisplayOrderRange(@Param("startOrder") Integer startOrder, @Param("endOrder") Integer endOrder);
    
    /**
     * Buscar colores más utilizados
     */
    @Query("SELECT c FROM Color c WHERE c.isActive = true ORDER BY c.productCount DESC, c.name ASC")
    List<Color> findMostUsedColors();
    
    /**
     * Buscar colores menos utilizados
     */
    @Query("SELECT c FROM Color c WHERE c.isActive = true ORDER BY c.productCount ASC, c.name ASC")
    List<Color> findLeastUsedColors();
    
    /**
     * Buscar colores sin productos
     */
    @Query("SELECT c FROM Color c WHERE c.isActive = true AND c.productCount = 0 ORDER BY c.displayOrder ASC, c.name ASC")
    List<Color> findUnusedColors();
    
    /**
     * Buscar colores con productos
     */
    @Query("SELECT c FROM Color c WHERE c.isActive = true AND c.productCount > 0 ORDER BY c.displayOrder ASC, c.name ASC")
    List<Color> findUsedColors();
    
    /**
     * Contar productos asociados a un color (como colorEntity o en la lista colores)
     */
    @Query("SELECT COUNT(DISTINCT p) FROM Product p " +
           "LEFT JOIN p.colores c " +
           "WHERE (p.colorEntity.id = :colorId OR c.id = :colorId) " +
           "AND p.activo = true")
    Long countProductsByColorId(@Param("colorId") Long colorId);
}
