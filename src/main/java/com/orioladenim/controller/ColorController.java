package com.orioladenim.controller;

import com.orioladenim.entity.Color;
import com.orioladenim.service.ColorService;
import com.orioladenim.service.ColorImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.Optional;

/**
 * Controlador para gestión de colores en el panel de administración
 */
@Controller
@RequestMapping("/admin/colors")
public class ColorController {
    
    @Autowired
    private ColorService colorService;
    
    @Autowired
    private ColorImageService colorImageService;
    
    /**
     * Listar todos los colores
     */
    @GetMapping
    public String listColors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        // Actualizar contadores de productos antes de mostrar la lista
        colorService.updateAllProductCounts();
        
        // Corregir colores con displayOrder = 0 o null
        colorService.fixColorsWithZeroOrder();
        
        // Configurar paginación y ordenamiento
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Color> colors;
        if (search != null && !search.trim().isEmpty()) {
            // Buscar colores
            List<Color> searchResults = colorService.searchColors(search);
            // Crear una página con los resultados de búsqueda
            colors = new org.springframework.data.domain.PageImpl<>(searchResults, pageable, searchResults.size());
            model.addAttribute("searchResults", searchResults);
        } else {
            // Obtener todos los colores paginados
            colors = colorService.getColorsPaginated(pageable);
        }
        
        model.addAttribute("title", "Gestión de Colores");
        model.addAttribute("colors", colors);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", colors.getTotalPages());
        model.addAttribute("totalItems", colors.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        
        return "admin/colors/list";
    }
    
    /**
     * Mostrar formulario para crear nuevo color
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("color", new Color());
        return "admin/colors/form";
    }
    
    /**
     * Mostrar formulario para editar color
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Color> color = colorService.getColorById(id);
        if (color.isPresent()) {
            model.addAttribute("color", color.get());
            return "admin/colors/form";
        } else {
            return "redirect:/admin/colors?error=color_not_found";
        }
    }
    
    /**
     * Crear nuevo color
     */
    @PostMapping("/create")
    public String createColor(
            @Valid @ModelAttribute("color") Color color,
            BindingResult bindingResult,
            @RequestParam(value = "patternImage", required = false) MultipartFile patternImage,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "admin/colors/form";
        }
        
        try {
            // Normalizar nombre antes de procesar (evitar strings vacíos)
            if (color.getName() != null) {
                String cleanName = color.getName().trim();
                if (cleanName.isEmpty()) {
                    bindingResult.rejectValue("name", "error.color", "El nombre del color es obligatorio");
                    return "admin/colors/form";
                }
                color.setName(cleanName);
            }
            
            // Si se subió una imagen, procesarla y guardarla
            if (patternImage != null && !patternImage.isEmpty()) {
                String imagePath = colorImageService.saveColorImage(patternImage, null);
                color.setImagePath(imagePath);
                // Si hay imagen, limpiar hexCode (opcional, pueden coexistir)
            }
            
            Color savedColor = colorService.createColor(color);
            
            // Si se guardó correctamente y hay imagen, actualizar con el ID real
            if (patternImage != null && !patternImage.isEmpty() && savedColor.getId() != null) {
                // La imagen ya se guardó con el ID null, está bien para nuevos colores
            }
            
            redirectAttributes.addFlashAttribute("success", "Color creado exitosamente");
            return "redirect:/admin/colors";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("name", "error.color", e.getMessage());
            return "admin/colors/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear color: " + e.getMessage());
            return "admin/colors/form";
        }
    }
    
    /**
     * Actualizar color existente
     */
    @PostMapping("/update/{id}")
    public String updateColor(
            @PathVariable Long id,
            @Valid @ModelAttribute("color") Color color,
            BindingResult bindingResult,
            @RequestParam(value = "patternImage", required = false) MultipartFile patternImage,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            // Recargar el color para mostrar la imagen actual
            Optional<Color> existingColor = colorService.getColorById(id);
            if (existingColor.isPresent()) {
                color.setImagePath(existingColor.get().getImagePath());
            }
            return "admin/colors/form";
        }
        
        try {
            // Obtener el color existente para preservar la imagen actual si no se cambia
            Optional<Color> existingColorOpt = colorService.getColorById(id);
            if (existingColorOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Color no encontrado");
                return "redirect:/admin/colors";
            }
            
            Color existingColor = existingColorOpt.get();
            String currentImagePath = existingColor.getImagePath();
            
            // Si se subió una nueva imagen, reemplazar la anterior automáticamente
            if (patternImage != null && !patternImage.isEmpty()) {
                // Eliminar imagen anterior si existe
                if (currentImagePath != null && !currentImagePath.isEmpty()) {
                    System.out.println("🗑️ Eliminando imagen anterior del color ID " + id + ": " + currentImagePath);
                    boolean deleted = colorImageService.deleteColorImage(currentImagePath);
                    if (deleted) {
                        System.out.println("✅ Imagen anterior eliminada correctamente");
                    } else {
                        System.out.println("⚠️ No se pudo eliminar la imagen anterior (puede que no exista): " + currentImagePath);
                    }
                }
                // Guardar nueva imagen
                System.out.println("💾 Guardando nueva imagen para color ID " + id);
                String imagePath = colorImageService.saveColorImage(patternImage, id);
                color.setImagePath(imagePath);
                System.out.println("✅ Nueva imagen guardada: " + imagePath);
            }
            // Si no se cambió nada, preservar la imagen actual
            else if (currentImagePath != null && !currentImagePath.isEmpty()) {
                color.setImagePath(currentImagePath);
            }
            
            colorService.updateColor(id, color);
            redirectAttributes.addFlashAttribute("success", "Color actualizado exitosamente");
            return "redirect:/admin/colors";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("name", "error.color", e.getMessage());
            return "admin/colors/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar color: " + e.getMessage());
            return "admin/colors/form";
        }
    }
    
    /**
     * Eliminar color (soft delete)
     */
    @PostMapping("/delete/{id}")
    public String deleteColor(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            colorService.deleteColor(id);
            redirectAttributes.addFlashAttribute("success", "Color eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/colors";
    }
    
    /**
     * Eliminar color permanentemente
     */
    @PostMapping("/delete-permanent/{id}")
    public String deleteColorPermanently(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            colorService.deleteColorPermanently(id);
            redirectAttributes.addFlashAttribute("success", "Color eliminado permanentemente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/colors";
    }
    
    
    /**
     * Reordenar colores
     */
    @PostMapping("/reorder")
    public String reorderColors(
            @RequestParam("colorIds") List<Long> colorIds,
            RedirectAttributes redirectAttributes) {
        
        try {
            colorService.reorderColors(colorIds);
            redirectAttributes.addFlashAttribute("success", "Orden de colores actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reordenar colores: " + e.getMessage());
        }
        
        return "redirect:/admin/colors";
    }
    
    /**
     * Crear o actualizar colores por defecto
     * Este endpoint asegura que los colores predeterminados estén marcados correctamente
     */
    @PostMapping("/create-defaults")
    public String createDefaultColors(RedirectAttributes redirectAttributes) {
        try {
            colorService.createDefaultColors();
            redirectAttributes.addFlashAttribute("success", "Colores predeterminados actualizados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar colores predeterminados: " + e.getMessage());
        }
        
        return "redirect:/admin/colors";
    }
    
    /**
     * API REST - Obtener colores activos (para AJAX)
     */
    @GetMapping("/api/active")
    @ResponseBody
    public List<Color> getActiveColors() {
        return colorService.getActiveColors();
    }
    
    /**
     * API REST - Buscar colores (para AJAX)
     */
    @GetMapping("/api/search")
    @ResponseBody
    public List<Color> searchColors(@RequestParam String search) {
        return colorService.searchColors(search);
    }
    
    /**
     * API REST - Obtener colores más utilizados
     */
    @GetMapping("/api/most-used")
    @ResponseBody
    public List<Color> getMostUsedColors() {
        return colorService.getMostUsedColors();
    }
    
    /**
     * API REST - Obtener colores sin usar
     */
    @GetMapping("/api/unused")
    @ResponseBody
    public List<Color> getUnusedColors() {
        return colorService.getUnusedColors();
    }
}
