package com.detodoya.controller;

import com.detodoya.entity.Historia;
import com.detodoya.service.HistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/admin/historias")
public class HistoriaController {
    
    @Autowired
    private HistoriaService historiaService;
    
    /**
     * Lista todas las historias
     */
    @GetMapping
    public String listarHistorias(Model model) {
        model.addAttribute("historias", historiaService.findAll());
        model.addAttribute("estadisticas", historiaService.obtenerEstadisticas());
        return "admin/historias/listar";
    }
    
    /**
     * Muestra el formulario para crear una nueva historia
     */
    @GetMapping("/nueva")
    public String nuevaHistoria(Model model) {
        model.addAttribute("historia", new Historia());
        return "admin/historias/formulario";
    }
    
    /**
     * Guarda una nueva historia
     */
    @PostMapping("/guardar")
    public String guardarHistoria(@RequestParam("titulo") String titulo,
                                 @RequestParam("descripcion") String descripcion,
                                 @RequestParam("video") MultipartFile video,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (video.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un video");
                return "redirect:/admin/historias/nueva";
            }
            
            historiaService.crearHistoria(titulo, descripcion, video);
            redirectAttributes.addFlashAttribute("success", "Historia creada exitosamente");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el video: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/historias";
    }
    
    /**
     * Muestra el formulario para editar una historia
     */
    @GetMapping("/editar/{id}")
    public String editarHistoria(@PathVariable Integer id, Model model) {
        Historia historia = historiaService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Historia no encontrada"));
        model.addAttribute("historia", historia);
        return "admin/historias/formulario-editar";
    }
    
    /**
     * Actualiza una historia existente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarHistoria(@PathVariable Integer id,
                                   @RequestParam("titulo") String titulo,
                                   @RequestParam("descripcion") String descripcion,
                                   @RequestParam(value = "video", required = false) MultipartFile video,
                                   RedirectAttributes redirectAttributes) {
        try {
            historiaService.actualizarHistoria(id, titulo, descripcion, video);
            redirectAttributes.addFlashAttribute("success", "Historia actualizada exitosamente");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el video: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/historias";
    }
    
    /**
     * Cambia el estado activo/inactivo de una historia
     */
    @PostMapping("/{id}/toggle")
    @ResponseBody
    public java.util.Map<String, Object> toggleActiva(@PathVariable Integer id) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            // Verificar si la historia está actualmente inactiva (se va a activar)
            Historia historiaActual = historiaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Historia no encontrada"));
            boolean seVaAActivar = !historiaActual.getActiva();
            
            Historia historia = historiaService.toggleActiva(id);
            String estado = historia.getActiva() ? "activada" : "desactivada";
            
            if (seVaAActivar) {
                // Si se activó, informar que se desactivaron las demás
                response.put("success", true);
                response.put("message", "Historia activada exitosamente. Las demás historias han sido desactivadas automáticamente.");
            } else {
                response.put("success", true);
                response.put("message", "Historia " + estado + " exitosamente");
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado de la historia: " + e.getMessage());
        }
        return response;
    }
    
    /**
     * Elimina una historia
     */
    @PostMapping("/{id}/eliminar")
    @ResponseBody
    public java.util.Map<String, Object> eliminarHistoria(@PathVariable Integer id) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            // Verificar que la historia existe
            Historia historia = historiaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Historia no encontrada"));
            
            historiaService.deleteById(id);
            response.put("success", true);
            response.put("message", "Historia eliminada exitosamente");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            response.put("success", false);
            response.put("message", "No se puede eliminar la historia porque tiene datos asociados.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la historia: " + e.getMessage());
        }
        return response;
    }
    
    /**
     * Muestra los detalles de una historia
     */
    @GetMapping("/{id}")
    public String verHistoria(@PathVariable Integer id, Model model) {
        Historia historia = historiaService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Historia no encontrada"));
        model.addAttribute("historia", historia);
        return "admin/historias/detalle";
    }
    
    /**
     * Busca historias
     */
    @GetMapping("/buscar")
    public String buscarHistorias(@RequestParam("q") String busqueda, Model model) {
        model.addAttribute("historias", historiaService.buscarHistorias(busqueda));
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadisticas", historiaService.obtenerEstadisticas());
        return "admin/historias/listar";
    }
    
    /**
     * Regenera el thumbnail de una historia específica
     * Útil después de instalar FFmpeg para regenerar thumbnails que eran placeholders
     */
    @PostMapping("/{id}/regenerar-thumbnail")
    public String regenerarThumbnail(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean exito = historiaService.regenerarThumbnail(id);
            if (exito) {
                redirectAttributes.addFlashAttribute("success", "Thumbnail regenerado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo regenerar el thumbnail. Verifique que FFmpeg esté instalado y que el video exista.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al regenerar thumbnail: " + e.getMessage());
        }
        return "redirect:/admin/historias";
    }
    
    /**
     * Regenera thumbnails de todas las historias
     */
    @PostMapping("/regenerar-todos-thumbnails")
    public String regenerarTodosThumbnails(RedirectAttributes redirectAttributes) {
        try {
            int exitosas = historiaService.regenerarTodosLosThumbnails();
            redirectAttributes.addFlashAttribute("success", 
                "Se regeneraron " + exitosas + " thumbnails exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al regenerar thumbnails: " + e.getMessage());
        }
        return "redirect:/admin/historias";
    }
}
