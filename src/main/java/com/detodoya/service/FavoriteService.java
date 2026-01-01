package com.detodoya.service;

import com.detodoya.entity.Favorite;
import com.detodoya.entity.Product;
import com.detodoya.entity.User;
import com.detodoya.repo.FavoriteRepository;
import com.detodoya.repo.ProductRepository;
import com.detodoya.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Agregar producto a favoritos
     * Soporta usuarios autenticados y sesiones anónimas
     */
    public boolean addFavorite(Integer productId, String sessionId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Intentar obtener usuario autenticado
        User user = getCurrentUser();
        
        if (user != null) {
            // Usuario autenticado
            if (favoriteRepository.existsByUserAndProduct(user, product)) {
                return false; // Ya existe
            }
            Favorite favorite = new Favorite(user, product);
            favoriteRepository.save(favorite);
            return true;
        } else {
            // Usuario anónimo (sesión)
            if (sessionId == null || sessionId.isEmpty()) {
                throw new RuntimeException("Session ID requerido para usuarios no autenticados");
            }
            if (favoriteRepository.existsBySessionIdAndProduct(sessionId, product)) {
                return false; // Ya existe
            }
            Favorite favorite = new Favorite(sessionId, product);
            favoriteRepository.save(favorite);
            return true;
        }
    }
    
    /**
     * Eliminar producto de favoritos
     */
    public boolean removeFavorite(Integer productId, String sessionId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        User user = getCurrentUser();
        
        if (user != null) {
            // Usuario autenticado
            favoriteRepository.deleteByUserAndProduct(user, product);
            return true;
        } else {
            // Usuario anónimo (sesión)
            if (sessionId == null || sessionId.isEmpty()) {
                throw new RuntimeException("Session ID requerido para usuarios no autenticados");
            }
            favoriteRepository.deleteBySessionIdAndProduct(sessionId, product);
            return true;
        }
    }
    
    /**
     * Verificar si un producto es favorito
     */
    @Transactional(readOnly = true)
    public boolean isFavorite(Integer productId, String sessionId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        User user = getCurrentUser();
        
        if (user != null) {
            return favoriteRepository.existsByUserAndProduct(user, product);
        } else {
            if (sessionId == null || sessionId.isEmpty()) {
                return false;
            }
            return favoriteRepository.existsBySessionIdAndProduct(sessionId, product);
        }
    }
    
    /**
     * Obtener todos los favoritos del usuario/sesión actual
     */
    @Transactional(readOnly = true)
    public List<Product> getFavorites(String sessionId) {
        User user = getCurrentUser();
        
        if (user != null) {
            List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);
            return favorites.stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
        } else {
            if (sessionId == null || sessionId.isEmpty()) {
                return List.of();
            }
            List<Favorite> favorites = favoriteRepository.findBySessionIdOrderByCreatedAtDesc(sessionId);
            return favorites.stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Obtener IDs de productos favoritos
     */
    @Transactional(readOnly = true)
    public List<Integer> getFavoriteProductIds(String sessionId) {
        User user = getCurrentUser();
        
        if (user != null) {
            return favoriteRepository.findProductIdsByUser(user);
        } else {
            if (sessionId == null || sessionId.isEmpty()) {
                return List.of();
            }
            return favoriteRepository.findProductIdsBySessionId(sessionId);
        }
    }
    
    /**
     * Contar favoritos del usuario/sesión actual
     */
    @Transactional(readOnly = true)
    public long countFavorites(String sessionId) {
        User user = getCurrentUser();
        
        if (user != null) {
            return favoriteRepository.countByUser(user);
        } else {
            if (sessionId == null || sessionId.isEmpty()) {
                return 0;
            }
            return favoriteRepository.countBySessionId(sessionId);
        }
    }
    
    /**
     * Obtener usuario autenticado actual (si existe)
     */
    private User getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Optional<User> userOpt = userRepository.findByUsername(auth.getName());
                return userOpt.orElse(null);
            }
        } catch (Exception e) {
            // Usuario no autenticado
        }
        return null;
    }
}

