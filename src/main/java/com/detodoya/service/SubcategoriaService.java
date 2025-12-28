package com.detodoya.service;

import com.detodoya.entity.Category;
import com.detodoya.entity.Product;
import com.detodoya.entity.Subcategoria;
import com.detodoya.repo.CategoryRepository;
import com.detodoya.repo.ProductRepository;
import com.detodoya.repo.SubcategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de subcategorías
 */
@Service
@Transactional
public class SubcategoriaService {
    
    @Autowired
    private SubcategoriaRepository subcategoriaRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Obtener todas las subcategorías activas ordenadas
     */
    @Transactional(readOnly = true)
    public List<Subcategoria> getActiveSubcategorias() {
        return subcategoriaRepository.findActiveSubcategoriasOrdered();
    }
    
    /**
     * Obtener todas las subcategorías (activas e inactivas)
     */
    @Transactional(readOnly = true)
    public List<Subcategoria> getAllSubcategorias() {
        return subcategoriaRepository.findAll();
    }
    
    /**
     * Buscar subcategoría por ID
     */
    @Transactional(readOnly = true)
    public Optional<Subcategoria> getSubcategoriaById(Long id) {
        return subcategoriaRepository.findById(id);
    }
    
    /**
     * Buscar subcategoría por ID (versión que devuelve Subcategoria directamente)
     */
    @Transactional(readOnly = true)
    public Subcategoria findById(Long id) {
        return getSubcategoriaById(id).orElse(null);
    }
    
    /**
     * Buscar subcategorías por categoría
     */
    @Transactional(readOnly = true)
    public List<Subcategoria> getSubcategoriasByCategory(Category category) {
        return subcategoriaRepository.findByCategory(category);
    }
    
    /**
     * Buscar subcategorías activas por ID de categoría
     */
    @Transactional(readOnly = true)
    public List<Subcategoria> getSubcategoriasByCategoryId(Long categoryId) {
        return subcategoriaRepository.findByCategoryId(categoryId);
    }
    
    /**
     * Buscar subcategorías activas por ID de categoría (alias para claridad)
     */
    @Transactional(readOnly = true)
    public List<Subcategoria> getActiveSubcategoriasByCategoryId(Long categoryId) {
        return subcategoriaRepository.findByCategoryId(categoryId);
    }
    
    /**
     * Buscar subcategoría por nombre
     */
    @Transactional(readOnly = true)
    public Optional<Subcategoria> getSubcategoriaByName(String name) {
        return subcategoriaRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Crear nueva subcategoría
     */
    public Subcategoria createSubcategoria(Subcategoria subcategoria) {
        // Validar que el nombre no exista en la misma categoría
        if (subcategoria.getCategory() != null && subcategoria.getCategory().getId() != null) {
            if (subcategoriaRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(
                    subcategoria.getName(), 
                    subcategoria.getCategory().getId(), 
                    -1L)) {
                throw new IllegalArgumentException("Ya existe una subcategoría con el nombre: " + subcategoria.getName() + " en esta categoría");
            }
        }
        
        // Asignar orden de visualización si no se especifica
        if (subcategoria.getDisplayOrder() == null && subcategoria.getCategory() != null && subcategoria.getCategory().getId() != null) {
            Integer nextOrder = subcategoriaRepository.getNextDisplayOrderForCategory(subcategoria.getCategory().getId());
            subcategoria.setDisplayOrder(nextOrder != null ? nextOrder : 1);
        }
        
        // Inicializar contador de productos
        if (subcategoria.getProductCount() == null) {
            subcategoria.setProductCount(0);
        }
        
        return subcategoriaRepository.save(subcategoria);
    }
    
    /**
     * Actualizar subcategoría existente
     */
    public Subcategoria updateSubcategoria(Long id, Subcategoria subcategoriaData) {
        System.out.println("🔄 [updateSubcategoria] Actualizando subcategoría ID: " + id);
        
        Subcategoria existingSubcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subcategoría no encontrada con ID: " + id));
        
        System.out.println("📦 [updateSubcategoria] Subcategoría encontrada: " + existingSubcategoria.getName());
        System.out.println("📦 [updateSubcategoria] Categoría actual: " + (existingSubcategoria.getCategory() != null ? existingSubcategoria.getCategory().getName() : "null"));
        System.out.println("📦 [updateSubcategoria] Nueva categoría: " + (subcategoriaData.getCategory() != null ? subcategoriaData.getCategory().getName() : "null"));
        
        // Validar que el nuevo nombre no exista en otra subcategoría de la misma categoría
        if (subcategoriaData.getCategory() != null && subcategoriaData.getCategory().getId() != null) {
            if (!existingSubcategoria.getName().equalsIgnoreCase(subcategoriaData.getName()) &&
                subcategoriaRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(
                    subcategoriaData.getName(), 
                    subcategoriaData.getCategory().getId(), 
                    id)) {
                throw new IllegalArgumentException("Ya existe otra subcategoría con el nombre: " + subcategoriaData.getName() + " en esta categoría");
            }
        }
        
        // Actualizar campos
        existingSubcategoria.setName(subcategoriaData.getName());
        existingSubcategoria.setDescription(subcategoriaData.getDescription());
        // Manejar isActive (si viene null del formulario, mantener el valor actual o establecer a true por defecto)
        if (subcategoriaData.getIsActive() != null) {
            existingSubcategoria.setIsActive(subcategoriaData.getIsActive());
        } else {
            // Si no viene el valor, mantener el actual o establecer a true por defecto
            if (existingSubcategoria.getIsActive() == null) {
                existingSubcategoria.setIsActive(true);
            }
        }
        existingSubcategoria.setDisplayOrder(subcategoriaData.getDisplayOrder());
        
        // Actualizar categoría si cambió
        if (subcategoriaData.getCategory() != null && 
            (existingSubcategoria.getCategory() == null || 
             !existingSubcategoria.getCategory().getId().equals(subcategoriaData.getCategory().getId()))) {
            System.out.println("🔄 [updateSubcategoria] Cambiando categoría de " + 
                (existingSubcategoria.getCategory() != null ? existingSubcategoria.getCategory().getName() : "null") + 
                " a " + subcategoriaData.getCategory().getName());
            existingSubcategoria.setCategory(subcategoriaData.getCategory());
        }
        
        Subcategoria saved = subcategoriaRepository.save(existingSubcategoria);
        System.out.println("✅ [updateSubcategoria] Subcategoría actualizada exitosamente");
        
        return saved;
    }
    
    /**
     * Eliminar subcategoría (eliminación en cascada)
     */
    public void deleteSubcategoria(Long id) {
        System.out.println("🗑️ [Service] Eliminando subcategoría ID: " + id);
        
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subcategoría no encontrada con ID: " + id));
        
        System.out.println("📦 [Service] Subcategoría encontrada: " + subcategoria.getName());
        
        // Obtener todos los productos asociados a esta subcategoría
        List<Product> productsWithSubcategoria = productRepository.findAll().stream()
                .filter(p -> p.getSubcategorias() != null && p.getSubcategorias().contains(subcategoria))
                .collect(java.util.stream.Collectors.toList());
        
        System.out.println("📦 [Service] Productos asociados encontrados: " + productsWithSubcategoria.size());
        
        // Remover la subcategoría de todos los productos asociados
        for (Product product : productsWithSubcategoria) {
            System.out.println("  - Removiendo subcategoría de producto: " + product.getName());
            product.getSubcategorias().remove(subcategoria);
            productRepository.save(product);
        }
        
        // Eliminar la subcategoría
        subcategoriaRepository.delete(subcategoria);
        
        System.out.println("✅ [Service] Subcategoría eliminada exitosamente");
    }
    
    /**
     * Activar/Desactivar subcategoría
     */
    public Subcategoria toggleSubcategoriaStatus(Long id) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subcategoría no encontrada con ID: " + id));
        
        subcategoria.setIsActive(!subcategoria.getIsActive());
        return subcategoriaRepository.save(subcategoria);
    }
    
    /**
     * Buscar subcategorías por texto
     */
    @Transactional(readOnly = true)
    public List<Subcategoria> searchSubcategorias(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getActiveSubcategorias();
        }
        return subcategoriaRepository.searchActiveSubcategorias(searchText.trim());
    }
    
    /**
     * Actualizar contador de productos de todas las subcategorías
     */
    @Transactional
    public void updateAllProductCounts() {
        List<Subcategoria> allSubcategorias = subcategoriaRepository.findAll();
        
        // Primero, resetear todos los contadores a 0
        for (Subcategoria subcategoria : allSubcategorias) {
            subcategoria.setProductCount(0);
        }
        
        // Luego, contar productos activos por subcategoría
        List<Product> activeProducts = productRepository.findByActivoTrue();
        for (Product product : activeProducts) {
            if (product.getSubcategorias() != null) {
                for (Subcategoria subcategoria : product.getSubcategorias()) {
                    subcategoria.incrementProductCount();
                }
            }
        }
        
        // Guardar todos los cambios
        subcategoriaRepository.saveAll(allSubcategorias);
    }
    
    /**
     * Obtener el número total de subcategorías
     */
    @Transactional(readOnly = true)
    public long getSubcategoriaCount() {
        return subcategoriaRepository.count();
    }
}

