package com.orioladenim.repo;

import com.orioladenim.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // Consultas básicas
    List<Contact> findByActivoTrueOrderByFechaCreacionDesc();
    List<Contact> findByLeidoFalseAndActivoTrueOrderByFechaCreacionDesc();
    List<Contact> findByRespondidoFalseAndActivoTrueOrderByFechaCreacionDesc();
    
    // Consultas con paginación
    Page<Contact> findByActivoTrueOrderByFechaCreacionDesc(Pageable pageable);
    Page<Contact> findByLeidoFalseAndActivoTrueOrderByFechaCreacionDesc(Pageable pageable);
    Page<Contact> findByRespondidoFalseAndActivoTrueOrderByFechaCreacionDesc(Pageable pageable);
    
    // Consultas por estado
    List<Contact> findByLeidoTrueAndRespondidoFalseAndActivoTrueOrderByFechaCreacionDesc();
    List<Contact> findByRespondidoTrueAndActivoTrueOrderByFechaCreacionDesc();
    
    // Consultas por fecha
    List<Contact> findByFechaCreacionBetweenAndActivoTrueOrderByFechaCreacionDesc(
        LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Consultas por email
    List<Contact> findByEmailAndActivoTrueOrderByFechaCreacionDesc(String email);
    
    // Consultas por producto de interés
    List<Contact> findByProductoInteresContainingIgnoreCaseAndActivoTrueOrderByFechaCreacionDesc(String producto);
    
    // Consultas de búsqueda
    @Query("SELECT c FROM Contact c WHERE c.activo = true AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.asunto) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.mensaje) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "ORDER BY c.fechaCreacion DESC")
    List<Contact> buscarPorTermino(@Param("termino") String termino);
    
    @Query("SELECT c FROM Contact c WHERE c.activo = true AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.asunto) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.mensaje) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "ORDER BY c.fechaCreacion DESC")
    Page<Contact> buscarPorTermino(@Param("termino") String termino, Pageable pageable);
    
    // Consultas de estadísticas
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.activo = true")
    long countTotalConsultas();
    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.leido = false AND c.activo = true")
    long countConsultasNoLeidas();
    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.respondido = false AND c.activo = true")
    long countConsultasNoRespondidas();
    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.fechaCreacion >= :fechaInicio AND c.activo = true")
    long countConsultasDesde(@Param("fechaInicio") LocalDateTime fechaInicio);
    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.fechaCreacion >= :fechaInicio AND c.fechaCreacion <= :fechaFin AND c.activo = true")
    long countConsultasEntre(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    // Consultas por producto más consultado
    @Query("SELECT c.productoInteres, COUNT(c) as cantidad FROM Contact c " +
           "WHERE c.activo = true AND c.productoInteres IS NOT NULL AND c.productoInteres != '' " +
           "GROUP BY c.productoInteres ORDER BY cantidad DESC")
    List<Object[]> findProductosMasConsultados();
    
    // Consultas recientes (últimas 24 horas)
    @Query("SELECT c FROM Contact c WHERE c.fechaCreacion >= :fechaInicio AND c.activo = true ORDER BY c.fechaCreacion DESC")
    List<Contact> findConsultasRecientes(@Param("fechaInicio") LocalDateTime fechaInicio);
    
    // Consultas por mes
    @Query("SELECT c FROM Contact c WHERE YEAR(c.fechaCreacion) = :año AND MONTH(c.fechaCreacion) = :mes AND c.activo = true ORDER BY c.fechaCreacion DESC")
    List<Contact> findConsultasPorMes(@Param("año") int año, @Param("mes") int mes);
    
    // Consultas por día
    @Query("SELECT c FROM Contact c WHERE DATE(c.fechaCreacion) = DATE(:fecha) AND c.activo = true ORDER BY c.fechaCreacion DESC")
    List<Contact> findConsultasPorDia(@Param("fecha") LocalDateTime fecha);
    
    // Consultas con filtros combinados
    @Query("SELECT c FROM Contact c WHERE c.activo = true AND " +
           "(:leido IS NULL OR c.leido = :leido) AND " +
           "(:respondido IS NULL OR c.respondido = :respondido) AND " +
           "(:fechaInicio IS NULL OR c.fechaCreacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR c.fechaCreacion <= :fechaFin) " +
           "ORDER BY c.fechaCreacion DESC")
    List<Contact> findConsultasConFiltros(
        @Param("leido") Boolean leido,
        @Param("respondido") Boolean respondido,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Consultas con filtros combinados y paginación
    @Query("SELECT c FROM Contact c WHERE c.activo = true AND " +
           "(:leido IS NULL OR c.leido = :leido) AND " +
           "(:respondido IS NULL OR c.respondido = :respondido) AND " +
           "(:fechaInicio IS NULL OR c.fechaCreacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR c.fechaCreacion <= :fechaFin) " +
           "ORDER BY c.fechaCreacion DESC")
    Page<Contact> findConsultasConFiltros(
        @Param("leido") Boolean leido,
        @Param("respondido") Boolean respondido,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );
}

