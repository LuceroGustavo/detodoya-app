package com.detodoya.controller;

import com.detodoya.entity.Category;
import com.detodoya.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para gestión de categorías en el panel de administración
 */
@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Listar todas las categorías
     */
    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        // Configurar paginación y ordenamiento
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Category> categories;
        if (search != null && !search.trim().isEmpty()) {
            // Buscar categorías
            List<Category> searchResults = categoryService.searchCategories(search);
            // Crear una página con los resultados de búsqueda
            categories = new org.springframework.data.domain.PageImpl<>(searchResults, pageable, searchResults.size());
            model.addAttribute("searchResults", searchResults);
        } else {
            // Obtener todas las categorías paginadas
            categories = categoryService.getCategoriesPaginated(pageable);
            
            // Debug: mostrar información de las categorías cargadas
            System.out.println("🔍 [DEBUG] Total de categorías en BD: " + categories.getTotalElements());
            System.out.println("🔍 [DEBUG] Categorías en esta página: " + categories.getContent().size());
            System.out.println("🔍 [DEBUG] Página actual: " + page + " de " + categories.getTotalPages());
            
            for (Category cat : categories.getContent()) {
                System.out.println("  - " + cat.getName() + " (ID: " + cat.getId() + ", Activa: " + cat.getIsActive() + ", Orden: " + cat.getDisplayOrder() + ")");
            }
        }
        
        // Debug: obtener todas las categorías para comparar
        List<Category> allCategories = categoryService.getAllCategories();
        System.out.println("🔍 [DEBUG] Todas las categorías en BD (sin paginación):");
        for (Category cat : allCategories) {
            System.out.println("  - " + cat.getName() + " (ID: " + cat.getId() + ", Activa: " + cat.getIsActive() + ", Orden: " + cat.getDisplayOrder() + ")");
        }
        
        model.addAttribute("title", "Gestión de Categorías");
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("totalItems", categories.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        
        return "admin/categories/list";
    }
    
    /**
     * Mostrar formulario para crear nueva categoría
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }
    
    /**
     * Mostrar formulario para editar categoría
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "admin/categories/form";
        } else {
            return "redirect:/admin/categories?error=category_not_found";
        }
    }
    
    /**
     * Crear nueva categoría
     */
    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "admin/categories/form";
        }
        
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Categoría creada exitosamente");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("name", "error.category", e.getMessage());
            return "admin/categories/form";
        }
    }
    
    /**
     * Crear nueva categoría (API JSON)
     */
    @PostMapping("/create-json")
    @ResponseBody
    public java.util.Map<String, Object> createCategoryJson(
            @Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult) {
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        System.out.println("🔄 Creando categoría JSON: " + category.getName());
        
        if (bindingResult.hasErrors()) {
            System.out.println("❌ Errores de validación: " + bindingResult.getAllErrors());
            response.put("success", false);
            response.put("message", "Errores de validación");
            response.put("errors", bindingResult.getAllErrors());
            return response;
        }
        
        try {
            Category savedCategory = categoryService.createCategory(category);
            System.out.println("✅ Categoría creada exitosamente con ID: " + savedCategory.getId());
            
            response.put("success", true);
            response.put("message", "Categoría creada exitosamente");
            response.put("categoryId", savedCategory.getId());
            // No devolver la entidad completa para evitar referencias circulares
            return response;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error al crear categoría: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/{id}/update-carousel-status")
    @ResponseBody
    public java.util.Map<String, Object> updateCarouselStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> request) {
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        try {
            Boolean showInCarousel = (Boolean) request.get("showInCarousel");
            System.out.println("🔄 [Backend] Actualizando estado del carrusel para categoría ID: " + id + " -> " + showInCarousel);
            
            Category category = categoryService.findById(id);
            if (category == null) {
                System.out.println("❌ [Backend] Categoría no encontrada con ID: " + id);
                response.put("success", false);
                response.put("message", "Categoría no encontrada");
                return response;
            }
            
            System.out.println("✅ [Backend] Categoría encontrada: " + category.getName() + " (showInCarousel actual: " + category.getShowInCarousel() + ")");
            
            category.setShowInCarousel(showInCarousel);
            Category updatedCategory = categoryService.updateCategory(id, category);
            
            System.out.println("✅ [Backend] Estado del carrusel actualizado: " + updatedCategory.getShowInCarousel());
            
            response.put("success", true);
            response.put("message", "Estado del carrusel actualizado correctamente");
            // No devolver la entidad completa para evitar referencias circulares
            return response;
            
        } catch (Exception e) {
            System.out.println("❌ [Backend] Error al actualizar estado del carrusel: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al actualizar estado del carrusel: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Actualizar categoría existente
     */
    @PostMapping("/update/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "admin/categories/form";
        }
        
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Categoría actualizada exitosamente");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("name", "error.category", e.getMessage());
            return "admin/categories/form";
        }
    }
    
    /**
     * Eliminar categoría (soft delete)
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public java.util.Map<String, Object> deleteCategory(@PathVariable Long id) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            categoryService.deleteCategory(id);
            response.put("success", true);
            response.put("message", "Categoría eliminada exitosamente");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            response.put("success", false);
            response.put("message", "No se puede eliminar la categoría porque tiene datos asociados (productos, imágenes, etc.).");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la categoría: " + e.getMessage());
        }
        return response;
    }
    
    /**
     * Eliminar categoría permanentemente
     */
    @PostMapping("/delete-permanent/{id}")
    public String deleteCategoryPermanently(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            categoryService.deleteCategoryPermanently(id);
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada permanentemente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
    
    /**
     * Activar/Desactivar categoría
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleCategoryStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            Category category = categoryService.toggleCategoryStatus(id);
            String status = category.getIsActive() ? "activada" : "desactivada";
            redirectAttributes.addFlashAttribute("success", "Categoría " + status + " exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
    
    /**
     * Ver detalles de categoría
     */
    @GetMapping("/view/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "admin/categories/view";
        } else {
            return "redirect:/admin/categories?error=category_not_found";
        }
    }
    
    /**
     * Reordenar categorías
     */
    @PostMapping("/reorder")
    public String reorderCategories(
            @RequestParam("categoryIds") List<Long> categoryIds,
            RedirectAttributes redirectAttributes) {
        
        try {
            categoryService.reorderCategories(categoryIds);
            redirectAttributes.addFlashAttribute("success", "Orden de categorías actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reordenar categorías: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
    
    /**
     * Crear categorías por defecto
     */
    @PostMapping("/create-defaults")
    public String createDefaultCategories(RedirectAttributes redirectAttributes) {
        try {
            categoryService.createDefaultCategories();
            redirectAttributes.addFlashAttribute("success", "Categorías por defecto creadas exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear categorías por defecto: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
    
    /**
     * API REST - Obtener categorías activas (para AJAX)
     */
    @GetMapping("/api/active")
    @ResponseBody
    public List<Map<String, Object>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return categories.stream()
                .map(cat -> {
                    Map<String, Object> categoryData = new java.util.HashMap<>();
                    categoryData.put("id", cat.getId());
                    categoryData.put("name", cat.getName());
                    categoryData.put("description", cat.getDescription());
                    categoryData.put("imagePath", cat.getImagePath());
                    categoryData.put("isActive", cat.getIsActive());
                    categoryData.put("displayOrder", cat.getDisplayOrder());
                    categoryData.put("productCount", cat.getProductCount());
                    categoryData.put("showInCarousel", cat.getShowInCarousel());
                    categoryData.put("carouselOrder", cat.getCarouselOrder());
                    return categoryData;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * API REST - Buscar categorías (para AJAX)
     */
    @GetMapping("/api/search")
    @ResponseBody
    public List<Map<String, Object>> searchCategories(@RequestParam String search) {
        List<Category> categories = categoryService.searchCategories(search);
        return categories.stream()
                .map(cat -> {
                    Map<String, Object> categoryData = new java.util.HashMap<>();
                    categoryData.put("id", cat.getId());
                    categoryData.put("name", cat.getName());
                    categoryData.put("description", cat.getDescription());
                    categoryData.put("imagePath", cat.getImagePath());
                    categoryData.put("isActive", cat.getIsActive());
                    categoryData.put("displayOrder", cat.getDisplayOrder());
                    categoryData.put("productCount", cat.getProductCount());
                    categoryData.put("showInCarousel", cat.getShowInCarousel());
                    categoryData.put("carouselOrder", cat.getCarouselOrder());
                    return categoryData;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    
    /**
     * API REST - Obtener estadísticas de ordenamiento
     */
    @GetMapping("/api/order-stats")
    @ResponseBody
    public Map<String, Object> getOrderStatistics() {
        return categoryService.getCategoryOrderStatistics();
    }
    
    /**
     * Actualizar contadores de productos de todas las categorías
     */
    @PostMapping("/update-product-counts")
    public String updateProductCounts(RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateAllProductCounts();
            redirectAttributes.addFlashAttribute("success", "Contadores de productos actualizados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar contadores: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
    
    /**
     * Toggle del estado de carrusel de una categoría
     */
    @PostMapping("/{id}/toggle-carrusel")
    @ResponseBody
    public Map<String, Object> toggleCarrusel(@PathVariable Long id) {
        try {
            Category category = categoryService.findById(id);
            if (category == null) {
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", false);
                response.put("message", "Categoría no encontrada");
                return response;
            }
            
            // Verificar que la categoría tenga imagen
            if (category.getImagePath() == null || category.getImagePath().trim().isEmpty()) {
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", false);
                response.put("message", "La categoría debe tener una imagen asociada para aparecer en el carrusel");
                return response;
            }
            
            // Cambiar el estado del carrusel
            category.setShowInCarousel(!category.getShowInCarousel());
            categoryService.updateCategory(category.getId(), category);
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", category.getShowInCarousel() ? 
                "Categoría agregada al carrusel del inicio" : 
                "Categoría removida del carrusel del inicio");
            response.put("showInCarousel", category.getShowInCarousel());
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del carrusel: " + e.getMessage());
            return response;
        }
    }
}
