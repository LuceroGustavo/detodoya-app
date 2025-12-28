package com.detodoya.controller;

import com.detodoya.entity.Category;
import com.detodoya.entity.Subcategoria;
import com.detodoya.service.CategoryService;
import com.detodoya.service.SubcategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para gestión de subcategorías en el panel de administración
 */
@Controller
@RequestMapping("/admin/subcategorias")
public class SubcategoriaController {
    
    @Autowired
    private SubcategoriaService subcategoriaService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Listar todas las subcategorías
     */
    @GetMapping
    public String listSubcategorias(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Model model) {
        
        List<Subcategoria> subcategorias;
        if (categoryId != null) {
            // Filtrar por categoría
            subcategorias = subcategoriaService.getSubcategoriasByCategoryId(categoryId);
            Category category = categoryService.findById(categoryId);
            model.addAttribute("selectedCategory", category);
        } else if (search != null && !search.trim().isEmpty()) {
            // Buscar subcategorías
            subcategorias = subcategoriaService.searchSubcategorias(search);
            model.addAttribute("searchResults", subcategorias);
        } else {
            // Obtener todas las subcategorías activas
            subcategorias = subcategoriaService.getActiveSubcategorias();
        }
        
        // Agregar todas las categorías para el filtro
        List<Category> categories = categoryService.getActiveCategories();
        
        model.addAttribute("title", "Gestión de Subcategorías");
        model.addAttribute("subcategorias", subcategorias);
        model.addAttribute("categories", categories);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);
        
        return "admin/subcategorias/list";
    }
    
    /**
     * Mostrar formulario para crear nueva subcategoría
     */
    @GetMapping("/new")
    public String showCreateForm(
            @RequestParam(required = false) Long categoryId,
            Model model) {
        Subcategoria subcategoria = new Subcategoria();
        
        // Si se especifica una categoría, asignarla por defecto
        if (categoryId != null) {
            Category category = categoryService.findById(categoryId);
            if (category != null) {
                subcategoria.setCategory(category);
            }
        }
        
        model.addAttribute("subcategoria", subcategoria);
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "admin/subcategorias/form";
    }
    
    /**
     * Mostrar formulario para editar subcategoría
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Subcategoria> subcategoria = subcategoriaService.getSubcategoriaById(id);
        if (subcategoria.isPresent()) {
            model.addAttribute("subcategoria", subcategoria.get());
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "admin/subcategorias/form";
        } else {
            return "redirect:/admin/subcategorias?error=subcategoria_not_found";
        }
    }
    
    /**
     * Crear nueva subcategoría
     */
    @PostMapping("/create")
    public String createSubcategoria(
            @Valid @ModelAttribute("subcategoria") Subcategoria subcategoria,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error en los datos de la subcategoría");
            return "redirect:/admin/subcategorias/new";
        }
        
        // Establecer la categoría si se proporciona el ID
        if (categoryId != null && (subcategoria.getCategory() == null || subcategoria.getCategory().getId() == null)) {
            Category category = categoryService.findById(categoryId);
            if (category != null) {
                subcategoria.setCategory(category);
            } else {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                return "redirect:/admin/subcategorias/new";
            }
        }
        
        try {
            subcategoriaService.createSubcategoria(subcategoria);
            redirectAttributes.addFlashAttribute("success", "Subcategoría creada exitosamente");
            return "redirect:/admin/subcategorias?categoryId=" + subcategoria.getCategory().getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/subcategorias/new";
        }
    }
    
    /**
     * Actualizar subcategoría existente
     */
    @PostMapping("/update/{id}")
    public String updateSubcategoria(
            @PathVariable Long id,
            @Valid @ModelAttribute("subcategoria") Subcategoria subcategoria,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error en los datos de la subcategoría");
            return "redirect:/admin/subcategorias/edit/" + id;
        }
        
        // Establecer la categoría si se proporciona el ID
        if (categoryId != null && (subcategoria.getCategory() == null || subcategoria.getCategory().getId() == null || !subcategoria.getCategory().getId().equals(categoryId))) {
            Category category = categoryService.findById(categoryId);
            if (category != null) {
                subcategoria.setCategory(category);
            } else {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                return "redirect:/admin/subcategorias/edit/" + id;
            }
        }
        
        try {
            subcategoriaService.updateSubcategoria(id, subcategoria);
            redirectAttributes.addFlashAttribute("success", "Subcategoría actualizada exitosamente");
            return "redirect:/admin/subcategorias?categoryId=" + subcategoria.getCategory().getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/subcategorias/edit/" + id;
        }
    }
    
    /**
     * Eliminar subcategoría
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    @Transactional
    public Map<String, Object> deleteSubcategoria(@PathVariable Long id) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            System.out.println("🗑️ [deleteSubcategoria] Eliminando subcategoría ID: " + id);
            
            Subcategoria subcategoria = subcategoriaService.findById(id);
            if (subcategoria == null) {
                response.put("success", false);
                response.put("message", "Subcategoría no encontrada");
                return response;
            }
            
            Long categoryId = subcategoria.getCategory() != null ? 
                subcategoria.getCategory().getId() : null;
            
            System.out.println("🗑️ [deleteSubcategoria] Subcategoría encontrada: " + subcategoria.getName());
            System.out.println("🗑️ [deleteSubcategoria] Categoría ID: " + categoryId);
            
            // Eliminar la subcategoría (el servicio maneja la eliminación de relaciones con productos)
            subcategoriaService.deleteSubcategoria(id);
            
            System.out.println("✅ [deleteSubcategoria] Subcategoría eliminada exitosamente");
            
            response.put("success", true);
            response.put("message", "Subcategoría eliminada exitosamente");
            response.put("categoryId", categoryId);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ [deleteSubcategoria] Error: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ [deleteSubcategoria] Error inesperado: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al eliminar la subcategoría: " + e.getMessage());
        }
        return response;
    }
    
    /**
     * Activar/Desactivar subcategoría
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleSubcategoriaStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            Subcategoria subcategoria = subcategoriaService.toggleSubcategoriaStatus(id);
            String status = subcategoria.getIsActive() ? "activada" : "desactivada";
            redirectAttributes.addFlashAttribute("success", "Subcategoría " + status + " exitosamente");
            
            Long categoryId = subcategoria.getCategory() != null ? subcategoria.getCategory().getId() : null;
            if (categoryId != null) {
                return "redirect:/admin/subcategorias?categoryId=" + categoryId;
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/subcategorias";
    }
    
    /**
     * API REST - Obtener subcategorías activas por categoría (para AJAX)
     */
    @GetMapping("/api/by-category/{categoryId}")
    @ResponseBody
    public List<Map<String, Object>> getSubcategoriasByCategory(@PathVariable Long categoryId) {
        // Obtener solo subcategorías activas
        List<Subcategoria> subcategorias = subcategoriaService.getActiveSubcategoriasByCategoryId(categoryId);
        return subcategorias.stream()
                .map(sub -> {
                    Map<String, Object> subcategoriaData = new java.util.HashMap<>();
                    subcategoriaData.put("id", sub.getId());
                    subcategoriaData.put("name", sub.getName());
                    subcategoriaData.put("description", sub.getDescription());
                    subcategoriaData.put("isActive", sub.getIsActive());
                    subcategoriaData.put("displayOrder", sub.getDisplayOrder());
                    subcategoriaData.put("productCount", sub.getProductCount());
                    subcategoriaData.put("categoryId", sub.getCategory() != null ? sub.getCategory().getId() : null);
                    subcategoriaData.put("categoryName", sub.getCategory() != null ? sub.getCategory().getName() : null);
                    return subcategoriaData;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * API REST - Obtener todas las subcategorías activas (para AJAX)
     */
    @GetMapping("/api/active")
    @ResponseBody
    public List<Map<String, Object>> getActiveSubcategorias() {
        List<Subcategoria> subcategorias = subcategoriaService.getActiveSubcategorias();
        return subcategorias.stream()
                .map(sub -> {
                    Map<String, Object> subcategoriaData = new java.util.HashMap<>();
                    subcategoriaData.put("id", sub.getId());
                    subcategoriaData.put("name", sub.getName());
                    subcategoriaData.put("description", sub.getDescription());
                    subcategoriaData.put("isActive", sub.getIsActive());
                    subcategoriaData.put("displayOrder", sub.getDisplayOrder());
                    subcategoriaData.put("productCount", sub.getProductCount());
                    subcategoriaData.put("categoryId", sub.getCategory() != null ? sub.getCategory().getId() : null);
                    subcategoriaData.put("categoryName", sub.getCategory() != null ? sub.getCategory().getName() : null);
                    return subcategoriaData;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}




