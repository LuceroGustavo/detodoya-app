package com.detodoya.service;

import com.detodoya.dto.ProductViewStats;
import com.detodoya.entity.Product;
import com.detodoya.entity.ProductView;
import com.detodoya.repo.ProductRepository;
import com.detodoya.repo.ProductViewRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Servicio para manejar analytics de productos
 * Incluye tracking de visitas y generación de estadísticas
 */
@Service
@Transactional
public class AnalyticsService {
    
    @Autowired
    private ProductViewRepository productViewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Trackea una visita a un producto
     * Incluye validaciones para prevenir spam
     */
    public void trackProductView(Integer productId, HttpServletRequest request) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String sessionId = request.getSession().getId();
            String referrer = request.getHeader("Referer");
            
            // Verificar si ya existe una vista reciente desde la misma IP (prevenir spam)
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            boolean hasRecentView = productViewRepository.existsRecentViewByIp(productId, ipAddress, oneHourAgo);
            
            if (!hasRecentView) {
                ProductView view = new ProductView(product, ipAddress, userAgent, sessionId, referrer);
                productViewRepository.save(view);
                
                System.out.println("📊 [ANALYTICS] Vista registrada para producto: " + product.getName() + 
                                 " desde IP: " + ipAddress);
            } else {
                System.out.println("⚠️ [ANALYTICS] Vista duplicada ignorada para producto: " + product.getName() + 
                                 " desde IP: " + ipAddress);
            }
            
        } catch (Exception e) {
            // Log error pero no interrumpir la experiencia del usuario
            System.err.println("❌ [ANALYTICS] Error tracking product view: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene los productos más visitados del mes actual
     */
    public List<ProductViewStats> getMostViewedProducts(int limit) {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
        
        return getMostViewedProductsSince(startOfMonth, limit);
    }
    
    /**
     * Obtiene los productos más visitados de la semana actual
     */
    public List<ProductViewStats> getMostViewedProductsThisWeek(int limit) {
        LocalDateTime startOfWeek = LocalDateTime.now()
            .minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
        
        return getMostViewedProductsSince(startOfWeek, limit);
    }
    
    /**
     * Obtiene los productos más visitados desde una fecha específica
     */
    public List<ProductViewStats> getMostViewedProductsSince(LocalDateTime startDate, int limit) {
        List<Object[]> results = productViewRepository.findMostViewedProductsSince(
            startDate, PageRequest.of(0, limit));
        
        return results.stream()
            .map(this::mapToProductViewStats)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los productos más visitados en un rango de fechas
     */
    public List<ProductViewStats> getMostViewedProductsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate, int limit) {
        
        List<Object[]> results = productViewRepository.findMostViewedProductsByDateRange(
            startDate, endDate, PageRequest.of(0, limit));
        
        return results.stream()
            .map(this::mapToProductViewStats)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las vistas por mes (últimos 6 meses)
     */
    public Map<String, Long> getViewsByMonth() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        
        List<Object[]> results = productViewRepository.getMonthlyViewsByDateRange(sixMonthsAgo);
        
        return results.stream()
            .collect(Collectors.toMap(
                result -> (String) result[0],
                result -> (Long) result[1],
                Long::sum
            ));
    }
    
    /**
     * Obtiene el total de vistas del mes actual
     */
    public Long getTotalViewsThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
        
        return productViewRepository.countViewsSince(startOfMonth);
    }
    
    /**
     * Obtiene el total de vistas de la semana actual
     */
    public Long getTotalViewsThisWeek() {
        LocalDateTime startOfWeek = LocalDateTime.now()
            .minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
        
        return productViewRepository.countViewsSince(startOfWeek);
    }
    
    /**
     * Obtiene el total de vistas en un rango de fechas
     */
    public Long getTotalViewsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return productViewRepository.countViewsByDateRange(startDate, endDate);
    }
    
    /**
     * Resetea todas las estadísticas de analytics
     */
    public void resetAnalytics() {
        try {
            long count = productViewRepository.count();
            System.out.println("🔄 [ANALYTICS] Reseteando " + count + " registros de vistas...");
            
            if (count > 0) {
                // Usar deleteAllInBatch para mejor rendimiento y evitar problemas de relaciones
                productViewRepository.deleteAllInBatch();
                System.out.println("✅ [ANALYTICS] Todas las estadísticas han sido reseteadas exitosamente");
            } else {
                System.out.println("ℹ️ [ANALYTICS] No hay estadísticas para resetear");
            }
            
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS] Error al resetear analytics: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al resetear analytics: " + e.getMessage(), e);
        }
    }

    /**
     * Genera datos de prueba para testing del dashboard
     */
    public void generateTestData() {
        try {
            System.out.println("🧪 [ANALYTICS] Generando datos de prueba...");
            
            // Obtener algunos productos existentes
            List<Product> products = productRepository.findAll();
            
            if (products.isEmpty()) {
                System.out.println("⚠️ [ANALYTICS] No hay productos en la base de datos para generar datos de prueba");
                return;
            }
            
            // Generar vistas de prueba para los últimos 30 días
            Random random = new Random();
            LocalDateTime now = LocalDateTime.now();
            
            for (int i = 0; i < 50; i++) {
                Product randomProduct = products.get(random.nextInt(products.size()));
                LocalDateTime randomDate = now.minusDays(random.nextInt(30));
                
                ProductView view = new ProductView(
                    randomProduct,
                    "192.168.1." + (100 + random.nextInt(50)), // IP simulada
                    "Mozilla/5.0 (Test Browser)", // User agent simulado
                    "session_" + random.nextInt(1000), // Session simulada
                    "https://example.com" // Referrer simulado
                );
                // Usar reflexión para establecer la fecha personalizada
                try {
                    java.lang.reflect.Field viewedAtField = ProductView.class.getDeclaredField("viewedAt");
                    viewedAtField.setAccessible(true);
                    viewedAtField.set(view, randomDate);
                } catch (Exception e) {
                    System.err.println("Error setting viewedAt: " + e.getMessage());
                }
                
                productViewRepository.save(view);
            }
            
            System.out.println("✅ [ANALYTICS] Datos de prueba generados exitosamente (50 vistas simuladas)");
            
        } catch (Exception e) {
            System.err.println("❌ [ANALYTICS] Error al generar datos de prueba: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar datos de prueba: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene estadísticas generales del sistema
     */
    public Map<String, Object> getGeneralStats() {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
        
        LocalDateTime startOfWeek = LocalDateTime.now()
            .minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0);
        
        return Map.of(
            "totalViewsThisMonth", getTotalViewsThisMonth(),
            "totalViewsThisWeek", getTotalViewsThisWeek(),
            "mostViewedThisMonth", getMostViewedProducts(5),
            "mostViewedThisWeek", getMostViewedProductsThisWeek(5),
            "monthlyViews", getViewsByMonth()
        );
    }
    
    /**
     * Convierte un resultado de consulta a ProductViewStats
     */
    private ProductViewStats mapToProductViewStats(Object[] result) {
        Product product = (Product) result[0];
        Long viewCount = (Long) result[1];
        
        ProductViewStats stats = new ProductViewStats();
        stats.setProduct(product);
        stats.setViewCount(viewCount);
        
        return stats;
    }
    
    /**
     * Obtiene la dirección IP real del cliente
     * Considera proxies y load balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
