package com.detodoya.service;

import com.detodoya.entity.Category;
import com.detodoya.entity.Product;
import com.detodoya.repo.CategoryRepository;
import com.detodoya.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de categorías
 * Contiene la lógica de negocio para operaciones de categorías
 */
@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryImageService categoryImageService;
    
    /**
     * Obtener todas las categorías activas ordenadas
     */
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findActiveCategoriesOrdered();
    }
    
    /**
     * Obtener todas las categorías (activas e inactivas)
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    /**
     * Obtener categorías paginadas
     */
    @Transactional(readOnly = true)
    public Page<Category> getCategoriesPaginated(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    /**
     * Buscar categoría por ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Buscar categoría por ID (versión que devuelve Category directamente)
     */
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return getCategoryById(id).orElse(null);
    }
    
    /**
     * Buscar categoría por nombre
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Crear nueva categoría
     */
    public Category createCategory(Category category) {
        // Validar que el nombre no exista
        if (categoryRepository.findByNameIgnoreCase(category.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + category.getName());
        }
        
        // Asignar orden de visualización si no se especifica
        if (category.getDisplayOrder() == null) {
            category.setDisplayOrder(categoryRepository.getNextDisplayOrder());
        }
        
        // Inicializar contador de productos
        if (category.getProductCount() == null) {
            category.setProductCount(0);
        }
        
        // Inicializar campos del carrusel
        if (category.getShowInCarousel() == null) {
            category.setShowInCarousel(false);
        }
        if (category.getCarouselOrder() == null) {
            category.setCarouselOrder(0);
        }
        
        Category savedCategory = categoryRepository.save(category);
        
        // Si se especificó un orden, aplicar reordenamiento inteligente después de guardar
        if (category.getDisplayOrder() != null) {
            reorderCategoriesIntelligently(savedCategory.getId(), category.getDisplayOrder());
        }
        
        // Verificar y corregir duplicados después de guardar
        fixDuplicateOrders();
        
        return savedCategory;
    }
    
    /**
     * Actualizar categoría existente
     */
    public Category updateCategory(Long id, Category categoryData) {
        System.out.println("🔄 [Service] Actualizando categoría ID: " + id);
        System.out.println("🔄 [Service] Datos recibidos - showInCarousel: " + categoryData.getShowInCarousel());
        
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        System.out.println("✅ [Service] Categoría encontrada - showInCarousel actual: " + existingCategory.getShowInCarousel());
        
        // Validar que el nuevo nombre no exista en otra categoría
        if (!existingCategory.getName().equalsIgnoreCase(categoryData.getName()) &&
            categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryData.getName(), id)) {
            throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + categoryData.getName());
        }
        
        // Actualizar campos
        existingCategory.setName(categoryData.getName());
        existingCategory.setDescription(categoryData.getDescription());
        existingCategory.setImagePath(categoryData.getImagePath());
        existingCategory.setIsActive(categoryData.getIsActive());
        
        // Actualizar campos del carrusel
        existingCategory.setShowInCarousel(categoryData.getShowInCarousel());
        existingCategory.setCarouselOrder(categoryData.getCarouselOrder());
        
        // Manejar el orden de visualización con reordenamiento inteligente
        Integer newOrder = categoryData.getDisplayOrder();
        if (newOrder != null && !newOrder.equals(existingCategory.getDisplayOrder())) {
            System.out.println("🔄 Reordenando categoría '" + existingCategory.getName() + "' a posición " + newOrder);
            reorderCategoriesIntelligently(id, newOrder);
            existingCategory.setDisplayOrder(newOrder);
        }
        
        System.out.println("🔄 [Service] Después de actualizar - showInCarousel: " + existingCategory.getShowInCarousel());
        
        Category savedCategory = categoryRepository.save(existingCategory);
        
        System.out.println("✅ [Service] Categoría guardada - showInCarousel final: " + savedCategory.getShowInCarousel());
        
        return savedCategory;
    }
    
    /**
     * Eliminar categoría (eliminación en cascada)
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        System.out.println("🗑️ Eliminando categoría: " + category.getName() + " (ID: " + id + ")");
        
        // Obtener todos los productos asociados a esta categoría
        List<Product> productsWithCategory = productRepository.findAll().stream()
                .filter(p -> p.getCategories().contains(category))
                .collect(java.util.stream.Collectors.toList());
        
        System.out.println("📦 Productos asociados encontrados: " + productsWithCategory.size());
        
        // Remover la categoría de todos los productos asociados
        for (Product product : productsWithCategory) {
            System.out.println("  - Removiendo categoría de producto: " + product.getName());
            product.getCategories().remove(category);
            productRepository.save(product);
        }
        
        // Eliminar imágenes físicas del sistema de archivos
        if (category.getImagePath() != null && !category.getImagePath().isEmpty()) {
            System.out.println("🖼️ Eliminando imagen de categoría: " + category.getImagePath());
            try {
                boolean deleted = categoryImageService.deleteCategoryImage(category.getImagePath());
                if (deleted) {
                    System.out.println("✅ Imagen de categoría eliminada del sistema de archivos");
                } else {
                    System.err.println("⚠️ No se pudo eliminar la imagen de categoría");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error al eliminar imagen de categoría: " + e.getMessage());
                // Continuar con la eliminación aunque falle la eliminación de la imagen
            }
        }
        
        // Ahora eliminar la categoría físicamente de la base de datos
        categoryRepository.delete(category);
        
        System.out.println("✅ Categoría eliminada exitosamente y removida de todos los productos");
    }
    
    /**
     * Eliminar categoría permanentemente
     */
    public void deleteCategoryPermanently(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        // Verificar si tiene productos
        if (category.hasProducts()) {
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        
        // Eliminar imágenes físicas del sistema de archivos
        if (category.getImagePath() != null && !category.getImagePath().isEmpty()) {
            System.out.println("🖼️ Eliminando imagen de categoría: " + category.getImagePath());
            try {
                boolean deleted = categoryImageService.deleteCategoryImage(category.getImagePath());
                if (deleted) {
                    System.out.println("✅ Imagen de categoría eliminada del sistema de archivos");
                } else {
                    System.err.println("⚠️ No se pudo eliminar la imagen de categoría");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error al eliminar imagen de categoría: " + e.getMessage());
                // Continuar con la eliminación aunque falle la eliminación de la imagen
            }
        }
        
        categoryRepository.delete(category);
    }
    
    /**
     * Activar/Desactivar categoría
     */
    public Category toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        category.setIsActive(!category.getIsActive());
        return categoryRepository.save(category);
    }
    
    /**
     * Buscar categorías por texto
     */
    @Transactional(readOnly = true)
    public List<Category> searchCategories(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getActiveCategories();
        }
        return categoryRepository.searchActiveCategories(searchText.trim());
    }
    
    /**
     * Obtener categorías listas para el carrusel (marcadas para carrusel Y con imagen)
     */
    @Transactional(readOnly = true)
    public List<Category> findReadyForCarousel() {
        return categoryRepository.findReadyForCarousel();
    }
    
    /**
     * Obtener categorías con productos
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesWithProducts() {
        // Usar fallback seguro para evitar problemas de transacción
        System.out.println("🔄 Obteniendo categorías con productos (modo seguro)");
        List<Category> allActiveCategories = categoryRepository.findActiveCategoriesOrdered();
        List<Category> categoriesWithProducts = new ArrayList<>();
        
        for (Category category : allActiveCategories) {
            // Verificar si la categoría tiene productos asociados
            boolean hasProducts = productRepository.findAll().stream()
                    .anyMatch(p -> p.getActivo() && p.getCategories().contains(category));
            
            if (hasProducts) {
                categoriesWithProducts.add(category);
                System.out.println("  ✅ " + category.getName() + " - Tiene productos");
            } else {
                System.out.println("  ❌ " + category.getName() + " - Sin productos");
            }
        }
        
        System.out.println("📊 Total categorías con productos: " + categoriesWithProducts.size());
        return categoriesWithProducts;
    }
    
    /**
     * Obtener categorías vacías
     */
    @Transactional(readOnly = true)
    public List<Category> getEmptyCategories() {
        return categoryRepository.findEmptyCategories();
    }
    
    /**
     * Actualizar orden de visualización
     */
    public void updateDisplayOrder(Long categoryId, Integer newOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + categoryId));
        
        category.setDisplayOrder(newOrder);
        categoryRepository.save(category);
    }
    
    /**
     * Reordenar categorías
     */
    public void reorderCategories(List<Long> categoryIds) {
        for (int i = 0; i < categoryIds.size(); i++) {
            updateDisplayOrder(categoryIds.get(i), i + 1);
        }
    }
    
    /**
     * Actualizar contador de productos de una categoría
     */
    public void updateProductCount(Long categoryId) {
        categoryRepository.updateProductCount(categoryId);
    }
    
    /**
     * Actualizar contador de productos de todas las categorías
     */
    @Transactional
    public void updateAllProductCounts() {
        System.out.println("🔄 Actualizando contadores de productos de todas las categorías...");
        
        List<Category> allCategories = categoryRepository.findAll();
        
        // Primero, resetear todos los contadores a 0
        for (Category category : allCategories) {
            category.setProductCount(0);
        }
        
        // Luego, contar productos activos por categoría
        List<Product> activeProducts = productRepository.findByActivoTrue();
        for (Product product : activeProducts) {
            for (Category category : product.getCategories()) {
                category.incrementProductCount();
            }
        }
        
        // Guardar todos los cambios
        categoryRepository.saveAll(allCategories);
        
        // Mostrar resultados
        for (Category category : allCategories) {
            System.out.println("  - " + category.getName() + ": " + category.getProductCount() + " productos");
        }
        
        System.out.println("✅ Contadores actualizados");
    }
    
    /**
     * Obtener estadísticas de categorías
     */
    @Transactional(readOnly = true)
    public Object[] getCategoryStatistics() {
        return categoryRepository.getCategoryStatistics();
    }
    
    /**
     * Crear categorías por defecto
     */
    public void createDefaultCategories() {
        // Verificar si ya existen categorías
        if (categoryRepository.count() > 0) {
            return;
        }
        
        // Crear categorías por defecto (solo tipos de prenda, no géneros ni temporadas)
        Category[] defaultCategories = {
            // CATEGORÍAS POR TIPO DE PRENDA
            new Category("Remeras", "Camisetas de algodón, básicas y estampadas para todas las temporadas"),
            new Category("Pantalones de Jean", "Jeans clásicos y modernos, diferentes cortes y lavados"),
            new Category("Buzos", "Buzos con capucha, oversize y básicos para invierno"),
            new Category("Camperas", "Abrigos y camperas para todas las estaciones"),
            new Category("Shorts", "Shorts de jean, básicos y desgastados para el verano"),
            new Category("Vestidos", "Vestidos casuales y formales para mujer"),
            new Category("Accesorios", "Cinturones, gorras, bolsos y otros accesorios"),
            
            // CATEGORÍA ESPECIAL
            new Category("Sin Categoría", "Productos sin clasificar temporalmente")
        };
        
        for (int i = 0; i < defaultCategories.length; i++) {
            defaultCategories[i].setDisplayOrder(i + 1);
            categoryRepository.save(defaultCategories[i]);
        }
    }
    
    /**
     * Validar datos de categoría
     */
    public void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        
        if (category.getName().length() < 2 || category.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres");
        }
        
        if (category.getDescription() != null && category.getDescription().length() > 200) {
            throw new IllegalArgumentException("La descripción no puede exceder 200 caracteres");
        }
        
        if (category.getImagePath() != null && category.getImagePath().length() > 500) {
            throw new IllegalArgumentException("La ruta de la imagen no puede exceder 500 caracteres");
        }
    }
    
    /**
     * Obtener el número total de categorías
     */
    @Transactional(readOnly = true)
    public long getCategoryCount() {
        return categoryRepository.count();
    }
    
    /**
     * Verificar si un orden de visualización ya está ocupado por otra categoría
     */
    @Transactional(readOnly = true)
    private boolean isDisplayOrderTaken(Integer order, Long excludeId) {
        return categoryRepository.findAll().stream()
                .anyMatch(c -> c.getDisplayOrder() != null && 
                              c.getDisplayOrder().equals(order) && 
                              !c.getId().equals(excludeId));
    }
    
    /**
     * Obtener el siguiente orden disponible
     */
    @Transactional(readOnly = true)
    private Integer getNextAvailableOrder() {
        Integer maxOrder = categoryRepository.getNextDisplayOrder();
        return maxOrder != null ? maxOrder : 1;
    }
    
    /**
     * Obtener el siguiente orden disponible para el carrusel
     */
    @Transactional(readOnly = true)
    public Integer getNextCarouselOrder() {
        List<Category> carouselCategories = categoryRepository.findReadyForCarousel();
        if (carouselCategories.isEmpty()) {
            return 1;
        }
        
        Integer maxOrder = carouselCategories.stream()
                .mapToInt(c -> c.getCarouselOrder() != null ? c.getCarouselOrder() : 0)
                .max()
                .orElse(0);
        
        return maxOrder + 1;
    }
    
    /**
     * Reordenamiento inteligente: cuando se asigna una posición específica,
     * mueve las demás categorías hacia abajo automáticamente
     */
    @Transactional
    public void reorderCategoriesIntelligently(Long categoryId, Integer newPosition) {
        try {
            System.out.println("🔧 Iniciando reordenamiento inteligente - Categoría ID: " + categoryId + ", Posición: " + newPosition);
            
            // Obtener todas las categorías excepto la que se está editando
            List<Category> allCategories = categoryRepository.findAll().stream()
                    .filter(c -> c.getDisplayOrder() != null && !c.getId().equals(categoryId))
                    .sorted((c1, c2) -> Integer.compare(c1.getDisplayOrder(), c2.getDisplayOrder()))
                    .collect(java.util.stream.Collectors.toList());
            
            System.out.println("📋 Categorías a reordenar: " + allCategories.size());
            
            // Mover categorías que están en la nueva posición o después
            boolean movedAny = false;
            for (Category category : allCategories) {
                Integer currentOrder = category.getDisplayOrder();
                if (currentOrder >= newPosition) {
                    Integer newOrder = currentOrder + 1;
                    System.out.println("⬇️ " + category.getName() + ": " + currentOrder + " → " + newOrder);
                    category.setDisplayOrder(newOrder);
                    categoryRepository.save(category);
                    movedAny = true;
                }
            }
            
            if (movedAny) {
                System.out.println("✅ Reordenamiento inteligente completado - Se movieron categorías");
            } else {
                System.out.println("ℹ️ Reordenamiento inteligente completado - No se movieron categorías");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en reordenamiento inteligente: " + e.getMessage());
            // No lanzar excepción para evitar rollback
        }
    }
    
    /**
     * Corregir órdenes duplicados reordenando automáticamente
     */
    @Transactional
    public void fixDuplicateOrders() {
        try {
            List<Category> allCategories = categoryRepository.findAll();
            
            // Agrupar por orden y encontrar duplicados
            Map<Integer, List<Category>> orderGroups = allCategories.stream()
                    .filter(c -> c.getDisplayOrder() != null)
                    .collect(java.util.stream.Collectors.groupingBy(Category::getDisplayOrder));
            
            boolean hasDuplicates = orderGroups.values().stream().anyMatch(list -> list.size() > 1);
            
            if (hasDuplicates) {
                System.out.println("🔧 Detectados órdenes duplicados. Reordenando automáticamente...");
                
                // Reordenar todas las categorías secuencialmente
                List<Category> sortedCategories = allCategories.stream()
                        .filter(c -> c.getDisplayOrder() != null)
                        .sorted((c1, c2) -> {
                            // Primero por orden, luego por ID para mantener consistencia
                            int orderCompare = Integer.compare(c1.getDisplayOrder(), c2.getDisplayOrder());
                            return orderCompare != 0 ? orderCompare : Long.compare(c1.getId(), c2.getId());
                        })
                        .collect(java.util.stream.Collectors.toList());
                
                // Asignar nuevos órdenes secuenciales
                for (int i = 0; i < sortedCategories.size(); i++) {
                    Category category = sortedCategories.get(i);
                    Integer newOrder = i + 1;
                    if (!newOrder.equals(category.getDisplayOrder())) {
                        System.out.println("🔄 " + category.getName() + ": " + category.getDisplayOrder() + " → " + newOrder);
                        category.setDisplayOrder(newOrder);
                        categoryRepository.save(category);
                    }
                }
                
                System.out.println("✅ Reordenamiento completado");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en corrección de duplicados: " + e.getMessage());
            // No lanzar excepción para evitar rollback
        }
    }
    
    /**
     * Obtener estadísticas de categorías
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCategoryOrderStatistics() {
        List<Category> allCategories = categoryRepository.findAll();
        Map<Integer, List<Category>> orderGroups = allCategories.stream()
                .filter(c -> c.getDisplayOrder() != null)
                .collect(java.util.stream.Collectors.groupingBy(Category::getDisplayOrder));
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalCategories", allCategories.size());
        stats.put("duplicateOrders", orderGroups.values().stream()
                .filter(list -> list.size() > 1)
                .mapToInt(List::size)
                .sum());
        stats.put("maxOrder", orderGroups.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0));
        stats.put("minOrder", orderGroups.keySet().stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElse(0));
        
        return stats;
    }
}
