package com.detodoya.repo;

import com.detodoya.entity.Historia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaRepository extends JpaRepository<Historia, Integer> {
    
    /**
     * Obtiene todas las historias activas ordenadas por fecha de creación descendente
     */
    List<Historia> findByActivaTrueOrderByFechaCreacionDesc();
    
    /**
     * Obtiene la historia activa más reciente (para mostrar en el index)
     */
    Optional<Historia> findFirstByActivaTrueOrderByFechaCreacionDesc();
    
    /**
     * Obtiene todas las historias activas (consulta personalizada)
     */
    @Query("SELECT h FROM Historia h WHERE h.activa = true ORDER BY h.fechaCreacion DESC")
    List<Historia> findHistoriasActivas();
    
    /**
     * Cuenta el total de historias activas
     */
    @Query("SELECT COUNT(h) FROM Historia h WHERE h.activa = true")
    Long countHistoriasActivas();
    
    /**
     * Obtiene historias por rango de fechas
     */
    @Query("SELECT h FROM Historia h WHERE h.activa = true AND h.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaCreacion DESC")
    List<Historia> findHistoriasActivasPorFecha(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin);
    
    /**
     * Busca historias por título o descripción
     */
    @Query("SELECT h FROM Historia h WHERE h.activa = true AND (LOWER(h.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(h.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))) ORDER BY h.fechaCreacion DESC")
    List<Historia> buscarHistoriasActivas(String busqueda);
}
