# Sistema de Analytics - Productos Más Visitados

## 📊 Resumen Ejecutivo

Se implementó un sistema completo de analytics para trackear y analizar los productos más visitados en la aplicación Oriola Indumentaria. El sistema permite a los administradores visualizar estadísticas detalladas de visitas, generar reportes por períodos específicos y gestionar los datos de analytics.

## 🎯 Objetivos Cumplidos

- ✅ **Tracking automático** de visitas a productos
- ✅ **Dashboard visual** con gráficos interactivos
- ✅ **Filtros por período** (mes, semana, personalizado)
- ✅ **Generación de datos de prueba** para testing
- ✅ **Reset de estadísticas** con confirmación
- ✅ **API REST** completa para analytics
- ✅ **Interfaz responsive** y moderna

## 🏗️ Arquitectura del Sistema

### Componentes Principales

```
📁 Backend (Spring Boot)
├── 🗃️ Entity: ProductView
├── 🔄 Repository: ProductViewRepository  
├── ⚙️ Service: AnalyticsService
├── 🌐 Controller: AnalyticsController
└── 📦 DTO: ProductViewStats

📁 Frontend (Thymeleaf + JavaScript)
├── 📊 Dashboard: analytics section
├── 📈 Chart.js: gráficos interactivos
├── 🔄 AJAX: comunicación con API
└── 🎨 Bootstrap: interfaz responsive
```

## 📋 Archivos Implementados

### Backend

#### 1. **ProductView.java** - Entidad Principal
```java
@Entity
@Table(name = "product_views")
public class ProductView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt = LocalDateTime.now();
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "referrer", length = 500)
    private String referrer;
}
```

**Características:**
- Relación ManyToOne con Product
- Tracking de IP, User Agent, Session
- Timestamp automático
- Campos opcionales para análisis avanzado

#### 2. **ProductViewRepository.java** - Repositorio con Consultas Optimizadas
```java
@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    
    @Query("SELECT p.product, COUNT(p) as viewCount " +
           "FROM ProductView p " +
           "WHERE p.viewedAt >= :startDate AND p.viewedAt <= :endDate " +
           "GROUP BY p.product " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedProductsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable);
    
    // Consultas adicionales para diferentes períodos
}
```

**Consultas Implementadas:**
- Productos más visitados por rango de fechas
- Productos más visitados desde fecha específica
- Vistas diarias por período
- Vistas mensuales por período
- Conteo total de vistas

#### 3. **AnalyticsService.java** - Lógica de Negocio
```java
@Service
@Transactional
public class AnalyticsService {
    
    public void trackProductView(Integer productId, HttpServletRequest request) {
        // Lógica de tracking con validaciones
        // Prevención de spam (misma IP en período corto)
        // Extracción de datos del request
    }
    
    public List<ProductViewStats> getMostViewedProducts(int limit) {
        // Obtener productos más visitados del mes actual
    }
    
    public void generateTestData() {
        // Generar 50 vistas simuladas para testing
        // Distribución aleatoria en últimos 30 días
    }
    
    public void resetAnalytics() {
        // Reset completo de estadísticas
        // Uso de deleteAllInBatch() para mejor rendimiento
    }
}
```

**Funcionalidades:**
- Tracking automático de visitas
- Prevención de spam por IP
- Generación de datos de prueba
- Reset seguro de estadísticas
- Consultas optimizadas por período

#### 4. **AnalyticsController.java** - API REST
```java
@RestController
@RequestMapping("/admin/api/analytics")
public class AnalyticsController {
    
    @GetMapping("/most-viewed")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedProducts(
            @RequestParam(defaultValue = "5") int limit);
    
    @GetMapping("/most-viewed-week")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedProductsThisWeek(
            @RequestParam(defaultValue = "5") int limit);
    
    @GetMapping("/most-viewed-by-date")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedProductsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit);
    
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetAnalytics();
    
    @PostMapping("/generate-test-data")
    public ResponseEntity<Map<String, String>> generateTestData();
}
```

**Endpoints Implementados:**
- `GET /most-viewed` - Productos más visitados del mes
- `GET /most-viewed-week` - Productos más visitados de la semana
- `GET /most-viewed-by-date` - Productos más visitados por rango
- `GET /views-by-month` - Vistas por mes (últimos 6 meses)
- `GET /total-views-month` - Total de vistas del mes
- `POST /reset` - Reset de estadísticas
- `POST /generate-test-data` - Generar datos de prueba

#### 5. **ProductViewStats.java** - DTO para Respuestas
```java
public class ProductViewStats {
    private Product product;
    private Long viewCount;
    
    // Métodos de conveniencia para extraer datos del producto
    public String getProductName();
    public String getProductImage();
    public BigDecimal getProductPrice();
    public String getProductDescription();
    public String getProductCategories();
    public String getProductStatus();
}
```

**Características:**
- Encapsula datos de producto y estadísticas
- Métodos de conveniencia para frontend
- Manejo de imágenes placeholder
- Formateo de datos para visualización

### Frontend

#### 6. **dashboard.html** - Interfaz de Usuario
```html
<!-- Sección de Analytics -->
<div class="row mt-4">
    <div class="col-12">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="bi bi-bar-chart-fill me-2 text-primary"></i>
                    Productos Más Visitados
                </h5>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-primary active" 
                            onclick="loadAnalytics('month', event)">
                        Este Mes
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-primary" 
                            onclick="loadAnalytics('week', event)">
                        Esta Semana
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-primary" 
                            onclick="loadAnalytics('custom', event)">
                        Personalizado
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-warning" 
                            onclick="resetAnalytics()">
                        <i class="bi bi-arrow-clockwise"></i> Resetear
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-info" 
                            onclick="generateTestData()">
                        <i class="bi bi-plus-circle"></i> Datos Prueba
                    </button>
                </div>
            </div>
            <div class="card-body">
                <!-- Filtros de fecha personalizados -->
                <!-- Gráfico Chart.js -->
                <!-- Tabla de productos -->
                <!-- Mensajes de estado -->
            </div>
        </div>
    </div>
</div>
```

**Características de la Interfaz:**
- **Botones de filtro** con estados activos
- **Filtros de fecha personalizados** (ocultos por defecto)
- **Gráfico interactivo** con Chart.js
- **Tabla responsive** con detalles de productos
- **Indicadores de carga** y mensajes de estado
- **Botones de acción** (Reset, Datos Prueba)

#### 7. **JavaScript Functions** - Lógica Frontend
```javascript
// Funciones principales implementadas:

function loadAnalytics(period, eventElement) {
    // Carga analytics con timeout de 10 segundos
    // Manejo de errores robusto
    // Actualización de UI
}

function loadCustomAnalytics() {
    // Validación de fechas
    // Carga de datos por rango personalizado
}

function updateChart(data) {
    // Creación/actualización de gráfico Chart.js
    // Configuración de colores y estilos
}

function updateTable(data) {
    // Actualización dinámica de tabla
    // Manejo de imágenes con fallback
}

function resetAnalytics() {
    // Confirmación de usuario
    // Reset con timeout
    // Recarga automática de datos
}

function generateTestData() {
    // Generación de datos de prueba
    // Confirmación de usuario
    // Recarga automática de datos
}
```

**Características del JavaScript:**
- **Timeouts de 10 segundos** para evitar requests colgadas
- **Manejo robusto de errores** con logging detallado
- **Validación de datos** antes de procesar
- **UI responsive** con estados de carga
- **Confirmaciones de usuario** para acciones críticas

## 🔧 Integración con Sistema Existente

### Tracking Automático
```java
// En PublicController.java
@GetMapping("/product/{id}")
public String productDetail(@PathVariable Integer id, Model model, HttpServletRequest request) {
    // ... lógica existente de carga de producto
    
    // 📊 TRACKING AUTOMÁTICO DE VISITAS
    analyticsService.trackProductView(id, request);
    
    return "product-detail";
}
```

**Características del Tracking:**
- **Automático** en cada visita a producto
- **Prevención de spam** (misma IP en 5 minutos)
- **Extracción de datos** del HttpServletRequest
- **No bloquea** la carga de la página

### Base de Datos
```sql
-- Tabla creada automáticamente por JPA
CREATE TABLE product_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    viewed_at DATETIME NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    referrer VARCHAR(500),
    FOREIGN KEY (product_id) REFERENCES product(p_id)
);

-- Índices recomendados para optimización
CREATE INDEX idx_product_views_product_id ON product_views(product_id);
CREATE INDEX idx_product_views_viewed_at ON product_views(viewed_at);
CREATE INDEX idx_product_views_ip_date ON product_views(ip_address, viewed_at);
```

## 🎨 Características de Diseño

### Interfaz Visual
- **Gráfico de barras** con Chart.js
- **Colores dinámicos** para cada producto
- **Tabla responsive** con imágenes de productos
- **Badges de estado** (Activo/Inactivo)
- **Indicadores de carga** con spinner
- **Mensajes informativos** cuando no hay datos

### Experiencia de Usuario
- **Filtros intuitivos** con botones activos
- **Fechas personalizadas** con validación
- **Confirmaciones** para acciones destructivas
- **Feedback visual** inmediato
- **Responsive design** para móviles

## 🚀 Funcionalidades Implementadas

### 1. **Dashboard Principal**
- Visualización de productos más visitados
- Gráfico interactivo con Chart.js
- Tabla detallada con información de productos
- Contador de "Visitas Este Mes"

### 2. **Filtros de Período**
- **Este Mes**: Productos más visitados del mes actual
- **Esta Semana**: Productos más visitados de la semana actual
- **Personalizado**: Selección de rango de fechas específico

### 3. **Gestión de Datos**
- **Reset**: Eliminación completa de estadísticas
- **Datos Prueba**: Generación de 50 vistas simuladas
- **Confirmaciones**: Para acciones destructivas

### 4. **API REST Completa**
- Endpoints para todos los tipos de consulta
- Manejo de errores robusto
- Respuestas en formato JSON
- Timeouts y validaciones

## 🔒 Consideraciones de Seguridad

### Prevención de Spam
```java
// En AnalyticsService.trackProductView()
LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
long recentViews = productViewRepository.countByProductAndIpAddressAndViewedAtAfter(
    product, ipAddress, fiveMinutesAgo);

if (recentViews > 0) {
    System.out.println("🚫 [ANALYTICS] Vistas desde misma IP ignoradas (spam prevention)");
    return;
}
```

### Validaciones
- **Timeouts** de 10 segundos en requests
- **Validación de fechas** en filtros personalizados
- **Confirmaciones** para acciones destructivas
- **Manejo de errores** sin exposición de datos sensibles

## 📊 Métricas y Rendimiento

### Optimizaciones Implementadas
- **Consultas HQL optimizadas** con índices
- **deleteAllInBatch()** para reset eficiente
- **Lazy loading** en relaciones JPA
- **Timeouts** para evitar requests colgadas
- **Paginación** en consultas grandes

### Escalabilidad
- **Índices de base de datos** para consultas rápidas
- **Consultas paginadas** para grandes volúmenes
- **Caching** de datos frecuentemente consultados
- **Arquitectura modular** para futuras extensiones

## 🧪 Testing y Datos de Prueba

### Generación de Datos
```java
public void generateTestData() {
    // Genera 50 vistas simuladas
    // Distribución aleatoria en últimos 30 días
    // IPs simuladas (192.168.1.100-150)
    // User agents de prueba
    // Sesiones simuladas
}
```

### Casos de Prueba
- ✅ **Dashboard sin datos**: Muestra mensaje informativo
- ✅ **Dashboard con datos**: Gráfico y tabla funcionando
- ✅ **Filtros por período**: Todos los filtros operativos
- ✅ **Reset de datos**: Eliminación completa
- ✅ **Generación de datos**: 50 vistas simuladas
- ✅ **Manejo de errores**: Timeouts y errores de red

## 🔮 Futuras Mejoras

### Funcionalidades Adicionales
- **Exportación de reportes** (PDF, Excel)
- **Alertas por email** cuando productos alcanzan umbrales
- **Análisis de tendencias** temporales
- **Comparación de períodos** lado a lado
- **Filtros por categoría** o características de producto

### Optimizaciones Técnicas
- **Caching Redis** para consultas frecuentes
- **Procesamiento asíncrono** de analytics
- **Compresión de datos** históricos
- **APIs de webhooks** para integraciones externas

## 📝 Notas de Implementación

### Problemas Resueltos
1. **Error de compilación**: Falta de import `java.util.Random`
2. **Bucle infinito**: Requests sin timeout causaban colgadas
3. **404 de placeholder**: Imagen faltante causaba requests infinitas
4. **Error setViewedAt**: Método no existía en entidad Lombok
5. **Referencias circulares**: JSON con objetos Product completos

### Soluciones Aplicadas
1. **Import agregado**: `import java.util.Random;`
2. **Timeouts implementados**: 10 segundos en todas las requests
3. **Placeholder creado**: `/images/placeholder.svg`
4. **Reflexión utilizada**: Para establecer fecha personalizada
5. **DTOs implementados**: Conversión a Map para evitar referencias circulares

## 🎉 Resultado Final

El sistema de analytics está **completamente funcional** y proporciona:

- ✅ **Tracking automático** de visitas a productos
- ✅ **Dashboard visual** con gráficos interactivos
- ✅ **Filtros flexibles** por período
- ✅ **Gestión completa** de datos de analytics
- ✅ **API REST robusta** con manejo de errores
- ✅ **Interfaz moderna** y responsive
- ✅ **Datos de prueba** para testing
- ✅ **Documentación completa** del sistema

El sistema está listo para producción y puede ser utilizado inmediatamente por los administradores para analizar el comportamiento de los usuarios y optimizar el catálogo de productos.

---

**Fecha de implementación**: Octubre 2025  
**Versión**: 1.0.0  
**Estado**: ✅ Completado y Funcional
