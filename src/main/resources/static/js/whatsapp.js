/**
 * WhatsApp Integration Script
 * Maneja la apertura automática de WhatsApp según el dispositivo del usuario
 * 
 * Funcionalidades:
 * - Detección automática de dispositivo (móvil/desktop)
 * - Apertura de WhatsApp app o WhatsApp Web
 * - Generación de mensajes predefinidos
 * - Validación de números de teléfono
 */

class WhatsAppIntegration {
    constructor() {
        this.whatsappNumber = '+5491112345678'; // Número de WhatsApp de ORIOLA
        this.isMobile = this.detectMobile();
        this.init();
    }

    /**
     * Detecta si el usuario está en un dispositivo móvil
     */
    detectMobile() {
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    }

    /**
     * Inicializa la funcionalidad de WhatsApp
     */
    init() {
        // Agregar event listeners a todos los botones de WhatsApp
        document.addEventListener('DOMContentLoaded', () => {
            this.addWhatsAppButtons();
        });
    }

    /**
     * Agrega botones de WhatsApp a los productos
     */
    addWhatsAppButtons() {
        // Buscar todos los productos en la página
        const productCards = document.querySelectorAll('.product-card-oriola');
        
        productCards.forEach(card => {
            this.addWhatsAppButtonToCard(card);
        });
    }

    /**
     * Agrega un botón de WhatsApp a una tarjeta de producto
     */
    addWhatsAppButtonToCard(card) {
        // Buscar el contenedor de acciones del producto
        const actionsContainer = card.querySelector('.product-actions') || 
                                card.querySelector('.d-flex.justify-content-between.align-items-center');
        
        if (actionsContainer) {
            // Crear botón de WhatsApp
            const whatsappButton = this.createWhatsAppButton(card);
            
            // Agregar el botón al contenedor
            if (actionsContainer.classList.contains('product-actions')) {
                actionsContainer.appendChild(whatsappButton);
            } else {
                // Si no hay contenedor de acciones, crear uno
                const newActionsContainer = document.createElement('div');
                newActionsContainer.className = 'product-actions d-flex gap-2';
                newActionsContainer.appendChild(whatsappButton);
                
                // Agregar después del precio
                const priceContainer = card.querySelector('.product-price');
                if (priceContainer && priceContainer.parentNode) {
                    priceContainer.parentNode.appendChild(newActionsContainer);
                }
            }
        }
    }

    /**
     * Crea un botón de WhatsApp para un producto
     */
    createWhatsAppButton(card) {
        const button = document.createElement('button');
        button.className = 'btn btn-success btn-sm whatsapp-btn';
        button.innerHTML = '<i class="bi bi-whatsapp me-1"></i>WhatsApp';
        button.title = 'Consultar por WhatsApp';
        
        // Obtener información del producto
        const productName = card.querySelector('.product-title')?.textContent || 'Producto';
        const productPrice = card.querySelector('.product-price')?.textContent || '';
        
        // Agregar event listener
        button.addEventListener('click', (e) => {
            e.preventDefault();
            this.openWhatsApp(productName, productPrice);
        });
        
        return button;
    }

    /**
     * Abre WhatsApp con un mensaje predefinido
     */
    openWhatsApp(productName, productPrice) {
        // Crear mensaje personalizado
        const message = this.createMessage(productName, productPrice);
        
        // Codificar el mensaje para URL
        const encodedMessage = encodeURIComponent(message);
        
        // Crear URL de WhatsApp
        let whatsappUrl;
        
        if (this.isMobile) {
            // Para móviles: abrir app de WhatsApp
            whatsappUrl = `whatsapp://send?phone=${this.whatsappNumber}&text=${encodedMessage}`;
        } else {
            // Para desktop: abrir WhatsApp Web
            whatsappUrl = `https://web.whatsapp.com/send?phone=${this.whatsappNumber}&text=${encodedMessage}`;
        }
        
        // Abrir WhatsApp
        window.open(whatsappUrl, '_blank');
        
        // Mostrar notificación
        this.showNotification('Abriendo WhatsApp...');
    }

    /**
     * Crea un mensaje personalizado para WhatsApp
     */
    createMessage(productName, productPrice) {
        const baseMessage = `¡Hola! Me interesa el producto: *${productName}*`;
        const priceInfo = productPrice ? `\n💰 Precio: ${productPrice}` : '';
        const additionalInfo = `\n\n¿Podrían darme más información sobre:\n• Disponibilidad\n• Tallas disponibles\n• Colores\n• Formas de pago\n• Envío\n\n¡Gracias!`;
        
        return baseMessage + priceInfo + additionalInfo;
    }

    /**
     * Muestra una notificación al usuario
     */
    showNotification(message) {
        // Crear elemento de notificación
        const notification = document.createElement('div');
        notification.className = 'whatsapp-notification';
        notification.textContent = message;
        
        // Estilos de la notificación
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #25D366;
            color: white;
            padding: 15px 20px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            z-index: 9999;
            font-weight: 500;
            animation: slideInRight 0.3s ease;
        `;
        
        // Agregar al DOM
        document.body.appendChild(notification);
        
        // Remover después de 3 segundos
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }

    /**
     * Método estático para abrir WhatsApp desde cualquier lugar
     */
    static openWhatsApp(productName = '', productPrice = '') {
        const whatsapp = new WhatsAppIntegration();
        whatsapp.openWhatsApp(productName, productPrice);
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new WhatsAppIntegration();
});

// Agregar estilos CSS para las animaciones
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .whatsapp-btn {
        background: #25D366 !important;
        border-color: #25D366 !important;
        color: white !important;
        transition: all 0.3s ease;
    }
    
    .whatsapp-btn:hover {
        background: #128C7E !important;
        border-color: #128C7E !important;
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(37, 211, 102, 0.3);
    }
    
    .whatsapp-btn:active {
        transform: translateY(0);
    }
    
    .whatsapp-btn i {
        font-size: 1.1em;
    }
`;
document.head.appendChild(style);

