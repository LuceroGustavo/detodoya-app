package com.detodoya.repo;

import com.detodoya.entity.ProductView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar las vistas de productos
 * Incluye consultas optimizadas para analytics
 */
@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    
    /**
     * Obtiene los productos más visitados desde una fecha específica
     * @param startDate Fecha de inicio
     * @param pageable Paginación
     * @return Lista de arrays [Product, Long] donde Long es el conteo de visitas
     */
    @Query("SELECT p.product, COUNT(p) as viewCount " +
           "FROM ProductView p " +
           "WHERE p.viewedAt >= :startDate " +
           "GROUP BY p.product " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedProductsSince(
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable);
    
    /**
     * Obtiene los productos más visitados en un rango de fechas
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param pageable Paginación
     * @return Lista de arrays [Product, Long] donde Long es el conteo de visitas
     */
    @Query("SELECT p.product, COUNT(p) as viewCount " +
           "FROM ProductView p " +
           "WHERE p.viewedAt >= :startDate AND p.viewedAt <= :endDate " +
           "GROUP BY p.product " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedProductsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable);
    
    /**
     * Obtiene las vistas diarias en un rango de fechas
     * @param startDate Fecha de inicio
     * @return Lista de arrays [LocalDate, Long] donde Long es el conteo de visitas diarias
     */
    @Query("SELECT DATE(p.viewedAt) as date, COUNT(p) as dailyViews " +
           "FROM ProductView p " +
           "WHERE p.viewedAt >= :startDate " +
           "GROUP BY DATE(p.viewedAt) " +
           "ORDER BY date DESC")
    List<Object[]> getDailyViewsByDateRange(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Obtiene las vistas mensuales desde una fecha específica
     * @param startDate Fecha de inicio
     * @return Lista de arrays [String, Long] donde String es el mes (YYYY-MM) y Long es el conteo
     */
    @Query("SELECT CONCAT(YEAR(p.viewedAt), '-', LPAD(CAST(MONTH(p.viewedAt) AS STRING), 2, '0')) as month, COUNT(p) as monthlyViews " +
           "FROM ProductView p " +
           "WHERE p.viewedAt >= :startDate " +
           "GROUP BY YEAR(p.viewedAt), MONTH(p.viewedAt) " +
           "ORDER BY month DESC")
    List<Object[]> getMonthlyViewsByDateRange(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Cuenta el total de vistas desde una fecha específica
     * @param startDate Fecha de inicio
     * @return Número total de vistas
     */
    @Query("SELECT COUNT(p) FROM ProductView p WHERE p.viewedAt >= :startDate")
    Long countViewsSince(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Cuenta el total de vistas en un rango de fechas
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Número total de vistas
     */
    @Query("SELECT COUNT(p) FROM ProductView p WHERE p.viewedAt >= :startDate AND p.viewedAt <= :endDate")
    Long countViewsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Verifica si ya existe una vista para un producto desde una IP en la última hora
     * Esto ayuda a prevenir conteos duplicados por spam
     * @param productId ID del producto
     * @param ipAddress Dirección IP
     * @param oneHourAgo Una hora atrás
     * @return true si existe una vista reciente
     */
    @Query("SELECT COUNT(p) > 0 FROM ProductView p " +
           "WHERE p.product.pId = :productId " +
           "AND p.ipAddress = :ipAddress " +
           "AND p.viewedAt >= :oneHourAgo")
    boolean existsRecentViewByIp(
        @Param("productId") Integer productId,
        @Param("ipAddress") String ipAddress,
        @Param("oneHourAgo") LocalDateTime oneHourAgo);
    
    /**
     * Elimina todas las vistas de un producto
     * @param productId ID del producto
     */
    @Modifying
    @Query("DELETE FROM ProductView p WHERE p.product.pId = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
    
    /**
     * Obtiene todas las vistas de un producto
     * @param productId ID del producto
     * @return Lista de vistas del producto
     */
    @Query("SELECT p FROM ProductView p WHERE p.product.pId = :productId")
    List<ProductView> findByProductId(@Param("productId") Integer productId);
}
