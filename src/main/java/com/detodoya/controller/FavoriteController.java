package com.detodoya.controller;

import com.detodoya.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * Agregar producto a favoritos
     */
    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @PathVariable Integer productId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            boolean added = favoriteService.addFavorite(productId, sessionId);
            
            if (added) {
                response.put("success", true);
                response.put("message", "Producto agregado a favoritos");
                response.put("isFavorite", true);
            } else {
                response.put("success", false);
                response.put("message", "El producto ya está en favoritos");
                response.put("isFavorite", true);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar a favoritos: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Eliminar producto de favoritos
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable Integer productId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            favoriteService.removeFavorite(productId, sessionId);
            
            response.put("success", true);
            response.put("message", "Producto eliminado de favoritos");
            response.put("isFavorite", false);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar de favoritos: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Verificar si un producto es favorito
     */
    @GetMapping("/{productId}/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable Integer productId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            boolean isFavorite = favoriteService.isFavorite(productId, sessionId);
            
            response.put("isFavorite", isFavorite);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("isFavorite", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Obtener todos los IDs de productos favoritos
     */
    @GetMapping("/ids")
    public ResponseEntity<Map<String, Object>> getFavoriteIds(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            List<Integer> favoriteIds = favoriteService.getFavoriteProductIds(sessionId);
            
            response.put("success", true);
            response.put("favoriteIds", favoriteIds);
            response.put("count", favoriteIds.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("favoriteIds", List.of());
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Toggle favorito (agregar si no existe, eliminar si existe)
     */
    @PostMapping("/{productId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Integer productId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = session.getId();
            boolean isFavorite = favoriteService.isFavorite(productId, sessionId);
            
            if (isFavorite) {
                favoriteService.removeFavorite(productId, sessionId);
                response.put("isFavorite", false);
                response.put("message", "Producto eliminado de favoritos");
            } else {
                favoriteService.addFavorite(productId, sessionId);
                response.put("isFavorite", true);
                response.put("message", "Producto agregado a favoritos");
            }
            
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

