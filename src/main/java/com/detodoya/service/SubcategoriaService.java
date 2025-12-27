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
        Subcategoria existingSubcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subcategoría no encontrada con ID: " + id));
        
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
        existingSubcategoria.setIsActive(subcategoriaData.getIsActive());
        existingSubcategoria.setDisplayOrder(subcategoriaData.getDisplayOrder());
        
        // Actualizar categoría si cambió
        if (subcategoriaData.getCategory() != null && 
            (existingSubcategoria.getCategory() == null || 
             !existingSubcategoria.getCategory().getId().equals(subcategoriaData.getCategory().getId()))) {
            existingSubcategoria.setCategory(subcategoriaData.getCategory());
        }
        
        return subcategoriaRepository.save(existingSubcategoria);
    }
    
    /**
     * Eliminar subcategoría (eliminación en cascada)
     */
    public void deleteSubcategoria(Long id) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subcategoría no encontrada con ID: " + id));
        
        // Obtener todos los productos asociados a esta subcategoría
        List<Product> productsWithSubcategoria = productRepository.findAll().stream()
                .filter(p -> p.getSubcategorias() != null && p.getSubcategorias().contains(subcategoria))
                .collect(java.util.stream.Collectors.toList());
        
        // Remover la subcategoría de todos los productos asociados
        for (Product product : productsWithSubcategoria) {
            product.getSubcategorias().remove(subcategoria);
            productRepository.save(product);
        }
        
        // Eliminar la subcategoría
        subcategoriaRepository.delete(subcategoria);
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

