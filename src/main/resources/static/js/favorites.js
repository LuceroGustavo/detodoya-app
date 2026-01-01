/**
 * Sistema de Favoritos para Detodoya.com
 * Maneja agregar/eliminar productos de favoritos usando sesión HTTP
 */

class FavoritesManager {
    constructor() {
        this.favoriteIds = new Set();
        this.initialized = false;
        this.init();
    }

    /**
     * Inicializar el sistema de favoritos
     */
    async init() {
        try {
            // Cargar favoritos existentes
            await this.loadFavorites();
            this.initialized = true;
            
            // Actualizar todos los botones de favoritos en la página
            this.updateAllFavoriteButtons();
        } catch (error) {
            console.error('Error al inicializar favoritos:', error);
        }
    }

    /**
     * Cargar IDs de productos favoritos desde el servidor
     */
    async loadFavorites() {
        try {
            const response = await fetch('/api/favorites/ids');
            const data = await response.json();
            
            if (data.success && data.favoriteIds) {
                this.favoriteIds = new Set(data.favoriteIds);
            }
        } catch (error) {
            console.error('Error al cargar favoritos:', error);
        }
    }

    /**
     * Verificar si un producto es favorito
     */
    isFavorite(productId) {
        return this.favoriteIds.has(productId);
    }

    /**
     * Toggle favorito (agregar si no existe, eliminar si existe)
     */
    async toggleFavorite(productId, buttonElement = null) {
        if (!this.initialized) {
            await this.init();
        }

        try {
            const response = await fetch(`/api/favorites/${productId}/toggle`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (data.success) {
                // Actualizar estado local
                if (data.isFavorite) {
                    this.favoriteIds.add(productId);
                } else {
                    this.favoriteIds.delete(productId);
                }

                // Actualizar botón si se proporciona
                if (buttonElement) {
                    this.updateFavoriteButton(buttonElement, data.isFavorite);
                } else {
                    // Actualizar todos los botones de este producto
                    this.updateProductFavoriteButtons(productId, data.isFavorite);
                }

                // Mostrar mensaje (opcional)
                if (data.message) {
                    this.showMessage(data.message, 'success');
                }

                return data.isFavorite;
            } else {
                throw new Error(data.message || 'Error al actualizar favorito');
            }
        } catch (error) {
            console.error('Error al toggle favorito:', error);
            this.showMessage('Error al actualizar favorito', 'error');
            return null;
        }
    }

    /**
     * Agregar producto a favoritos
     */
    async addFavorite(productId, buttonElement = null) {
        if (this.isFavorite(productId)) {
            return true; // Ya es favorito
        }

        try {
            const response = await fetch(`/api/favorites/${productId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (data.success || data.isFavorite) {
                this.favoriteIds.add(productId);
                if (buttonElement) {
                    this.updateFavoriteButton(buttonElement, true);
                }
                return true;
            }
            return false;
        } catch (error) {
            console.error('Error al agregar favorito:', error);
            return false;
        }
    }

    /**
     * Eliminar producto de favoritos
     */
    async removeFavorite(productId, buttonElement = null) {
        if (!this.isFavorite(productId)) {
            return true; // Ya no es favorito
        }

        try {
            const response = await fetch(`/api/favorites/${productId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (data.success || !data.isFavorite) {
                this.favoriteIds.delete(productId);
                if (buttonElement) {
                    this.updateFavoriteButton(buttonElement, false);
                }
                return true;
            }
            return false;
        } catch (error) {
            console.error('Error al eliminar favorito:', error);
            return false;
        }
    }

    /**
     * Actualizar estado visual de un botón de favorito
     */
    updateFavoriteButton(button, isFavorite) {
        if (!button) return;

        const icon = button.querySelector('.material-symbols-outlined, .favorite-icon');
        
        if (isFavorite) {
            button.classList.add('favorite-active');
            button.classList.remove('favorite-inactive');
            if (icon) {
                icon.textContent = 'favorite';
                icon.style.fontVariationSettings = "'FILL' 1";
            }
            button.setAttribute('aria-label', 'Eliminar de favoritos');
            button.title = 'Eliminar de favoritos';
        } else {
            button.classList.remove('favorite-active');
            button.classList.add('favorite-inactive');
            if (icon) {
                icon.textContent = 'favorite';
                icon.style.fontVariationSettings = "'FILL' 0";
            }
            button.setAttribute('aria-label', 'Agregar a favoritos');
            button.title = 'Agregar a favoritos';
        }
    }

    /**
     * Actualizar todos los botones de favorito de un producto específico
     */
    updateProductFavoriteButtons(productId, isFavorite) {
        const buttons = document.querySelectorAll(`[data-product-id="${productId}"].favorite-btn`);
        buttons.forEach(button => {
            this.updateFavoriteButton(button, isFavorite);
        });
    }

    /**
     * Actualizar todos los botones de favorito en la página
     */
    updateAllFavoriteButtons() {
        const buttons = document.querySelectorAll('.favorite-btn[data-product-id]');
        buttons.forEach(button => {
            const productId = parseInt(button.getAttribute('data-product-id'));
            if (productId) {
                const isFavorite = this.isFavorite(productId);
                this.updateFavoriteButton(button, isFavorite);
            }
        });
    }

    /**
     * Mostrar mensaje temporal (opcional, puede usar DetodoyaMessages si existe)
     */
    showMessage(message, type = 'info') {
        if (typeof DetodoyaMessages !== 'undefined' && DetodoyaMessages.showSuccess) {
            if (type === 'success') {
                DetodoyaMessages.showSuccess(message);
            } else if (type === 'error') {
                DetodoyaMessages.showError(message);
            } else {
                DetodoyaMessages.showInfo(message);
            }
        } else {
            // Fallback simple
            console.log(`[${type.toUpperCase()}] ${message}`);
        }
    }
}

// Inicializar cuando el DOM esté listo
let favoritesManager = null;

document.addEventListener('DOMContentLoaded', function() {
    favoritesManager = new FavoritesManager();
    window.favoritesManager = favoritesManager; // Exponer inmediatamente
    
    // Agregar event listeners a todos los botones de favorito
    document.addEventListener('click', function(e) {
        const favoriteBtn = e.target.closest('.favorite-btn');
        if (favoriteBtn) {
            e.preventDefault();
            e.stopPropagation();
            
            const productId = parseInt(favoriteBtn.getAttribute('data-product-id'));
            if (productId && favoritesManager) {
                favoritesManager.toggleFavorite(productId, favoriteBtn);
            }
        }
    });
});

// Exponer globalmente para uso manual si es necesario
window.FavoritesManager = FavoritesManager;

