package com.orioladenim.controller;

import com.orioladenim.dto.ProductViewStats;
import com.orioladenim.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para analytics de productos
 * Proporciona endpoints para obtener estadísticas de visitas
 */
@RestController
@RequestMapping("/admin/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    /**
     * Obtiene los productos más visitados del mes actual
     */
    @GetMapping("/most-viewed")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedProducts(
            @RequestParam(defaultValue = "5") int limit) {
        
        try {
            List<ProductViewStats> stats = analyticsService.getMostViewedProducts(limit);
            List<Map<String, Object>> response = stats.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting most viewed products: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene los productos más visitados de la semana actual
     */
    @GetMapping("/most-viewed-week")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedProductsThisWeek(
            @RequestParam(defaultValue = "5") int limit) {
        
        try {
            List<ProductViewStats> stats = analyticsService.getMostViewedProductsThisWeek(limit);
            List<Map<String, Object>> response = stats.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting most viewed products this week: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene los productos más visitados en un rango de fechas
     */
    @GetMapping("/most-viewed-by-date")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedProductsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit) {
        
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            
            List<ProductViewStats> stats = analyticsService.getMostViewedProductsByDateRange(
                startDateTime, endDateTime, limit);
            List<Map<String, Object>> response = stats.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting most viewed products by date range: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene las vistas por mes (últimos 6 meses)
     */
    @GetMapping("/views-by-month")
    public ResponseEntity<Map<String, Long>> getViewsByMonth() {
        try {
            Map<String, Long> monthlyViews = analyticsService.getViewsByMonth();
            return ResponseEntity.ok(monthlyViews);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting views by month: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene el total de vistas del mes actual
     */
    @GetMapping("/total-views-month")
    public ResponseEntity<Long> getTotalViewsThisMonth() {
        try {
            Long totalViews = analyticsService.getTotalViewsThisMonth();
            return ResponseEntity.ok(totalViews);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting total views this month: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene el total de vistas de la semana actual
     */
    @GetMapping("/total-views-week")
    public ResponseEntity<Long> getTotalViewsThisWeek() {
        try {
            Long totalViews = analyticsService.getTotalViewsThisWeek();
            return ResponseEntity.ok(totalViews);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting total views this week: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene el total de vistas en un rango de fechas
     */
    @GetMapping("/total-views-by-date")
    public ResponseEntity<Long> getTotalViewsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            
            Long totalViews = analyticsService.getTotalViewsByDateRange(startDateTime, endDateTime);
            return ResponseEntity.ok(totalViews);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting total views by date range: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene estadísticas generales del sistema
     */
    @GetMapping("/general-stats")
    public ResponseEntity<Map<String, Object>> getGeneralStats() {
        try {
            Map<String, Object> stats = analyticsService.getGeneralStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error getting general stats: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Resetea todas las estadísticas de analytics
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetAnalytics() {
        try {
            analyticsService.resetAnalytics();
            return ResponseEntity.ok(Map.of("message", "Analytics reseteados exitosamente"));
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error resetting analytics: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al resetear analytics: " + e.getMessage()));
        }
    }

    @PostMapping("/generate-test-data")
    public ResponseEntity<Map<String, String>> generateTestData() {
        try {
            analyticsService.generateTestData();
            return ResponseEntity.ok(Map.of("message", "Datos de prueba generados exitosamente"));
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS-API] Error generating test data: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al generar datos de prueba: " + e.getMessage()));
        }
    }
    
    /**
     * Convierte ProductViewStats a Map para evitar referencias circulares en JSON
     */
    private Map<String, Object> convertToMap(ProductViewStats stats) {
        return Map.of(
            "productId", stats.getProductId(),
            "productName", stats.getProductName(),
            "productImage", stats.getProductImage(),
            "productPrice", stats.getProductPrice(),
            "productDescription", stats.getProductDescription(),
            "productCategories", stats.getProductCategories(),
            "productStatus", stats.getProductStatus(),
            "viewCount", stats.getViewCount()
        );
    }
}
