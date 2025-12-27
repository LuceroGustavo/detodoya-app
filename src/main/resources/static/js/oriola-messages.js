/**
 * Sistema de Mensajes Detodoya
 * Muestra mensajes de error y éxito con estilo minimalista
 * "Detodoya dice:" - Rojo para errores, Verde para éxitos
 */

class DetodoyaMessages {
    constructor() {
        this.init();
    }

    init() {
        // Crear contenedor de mensajes si no existe
        if (!document.getElementById('detodoya-messages-container')) {
            const container = document.createElement('div');
            container.id = 'detodoya-messages-container';
            container.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 10000; max-width: 400px;';
            document.body.appendChild(container);
        }
    }

    /**
     * Muestra un mensaje de error (rojo)
     * @param {string} message - Mensaje a mostrar
     * @param {number} duration - Duración en milisegundos (default: 5000)
     */
    showError(message, duration = 5000) {
        this.showMessage(message, 'error', duration);
    }

    /**
     * Muestra un mensaje de éxito (verde)
     * @param {string} message - Mensaje a mostrar
     * @param {number} duration - Duración en milisegundos (default: 5000)
     */
    showSuccess(message, duration = 5000) {
        this.showMessage(message, 'success', duration);
    }

    /**
     * Muestra un mensaje de información (azul)
     * @param {string} message - Mensaje a mostrar
     * @param {number} duration - Duración en milisegundos (default: 5000)
     */
    showInfo(message, duration = 5000) {
        this.showMessage(message, 'info', duration);
    }

    /**
     * Muestra un mensaje de advertencia (amarillo)
     * @param {string} message - Mensaje a mostrar
     * @param {number} duration - Duración en milisegundos (default: 5000)
     */
    showWarning(message, duration = 5000) {
        this.showMessage(message, 'warning', duration);
    }

    /**
     * Función principal para mostrar mensajes
     * @param {string} message - Mensaje a mostrar
     * @param {string} type - Tipo: 'error', 'success', 'info', 'warning'
     * @param {number} duration - Duración en milisegundos
     */
    showMessage(message, type = 'info', duration = 5000) {
        const container = document.getElementById('detodoya-messages-container');
        if (!container) {
            this.init();
            return this.showMessage(message, type, duration);
        }

        // Crear elemento del mensaje
        const messageDiv = document.createElement('div');
        messageDiv.className = `oriola-message oriola-message-${type}`;
        
        // Icono según el tipo
        let icon = '';
        switch(type) {
            case 'error':
                icon = '✕';
                break;
            case 'success':
                icon = '✓';
                break;
            case 'info':
                icon = 'ℹ';
                break;
            case 'warning':
                icon = '⚠';
                break;
        }

        messageDiv.innerHTML = `
            <div class="oriola-message-content">
                <div class="oriola-message-header">
                    <span class="oriola-message-title">Oriola dice:</span>
                </div>
                <div class="oriola-message-body">
                    <span class="oriola-message-icon">${icon}</span>
                    <span class="oriola-message-text">${this.escapeHtml(message)}</span>
                </div>
            </div>
            <button type="button" class="oriola-message-close" onclick="this.parentElement.remove()">×</button>
        `;

        // Agregar al contenedor
        container.appendChild(messageDiv);

        // Animación de entrada
        setTimeout(() => {
            messageDiv.classList.add('oriola-message-show');
        }, 10);

        // Auto-eliminar después de la duración
        if (duration > 0) {
            setTimeout(() => {
                messageDiv.classList.remove('oriola-message-show');
                setTimeout(() => {
                    if (messageDiv.parentNode) {
                        messageDiv.remove();
                    }
                }, 300);
            }, duration);
        }
    }

    /**
     * Escapa HTML para prevenir XSS
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * Limpia todos los mensajes
     */
    clearAll() {
        const container = document.getElementById('detodoya-messages-container');
        if (container) {
            container.innerHTML = '';
        }
    }

    /**
     * Muestra un diálogo de confirmación con estilo Oriola (amarillo/advertencia)
     * @param {string} message - Mensaje a mostrar
     * @param {string} confirmText - Texto del botón de confirmación (default: "Aceptar")
     * @param {string} cancelText - Texto del botón de cancelación (default: "Cancelar")
     * @returns {Promise<boolean>} - Promise que resuelve a true si se confirma, false si se cancela
     */
    confirm(message, confirmText = 'Aceptar', cancelText = 'Cancelar') {
        return new Promise((resolve) => {
            // Crear overlay
            const overlay = document.createElement('div');
            overlay.className = 'oriola-confirm-overlay';
            overlay.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0, 0, 0, 0.5);
                z-index: 10001;
                display: flex;
                align-items: center;
                justify-content: center;
                animation: fadeIn 0.2s ease-in-out;
            `;

            // Crear diálogo
            const dialog = document.createElement('div');
            dialog.className = 'oriola-confirm-dialog';
            dialog.style.cssText = `
                background: #ffffff;
                border-radius: 8px;
                box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
                max-width: 450px;
                width: 90%;
                padding: 0;
                overflow: hidden;
                animation: slideDown 0.3s ease-out;
                border-left: 4px solid #ffc107;
            `;

            dialog.innerHTML = `
                <div style="padding: 24px;">
                    <div style="margin-bottom: 12px;">
                        <span style="font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; color: #6c757d;">Oriola dice:</span>
                    </div>
                    <div style="display: flex; align-items: flex-start; gap: 12px; margin-bottom: 24px;">
                        <span style="font-size: 24px; color: #ffc107; flex-shrink: 0; margin-top: 2px;">⚠</span>
                        <span style="font-size: 15px; line-height: 1.5; color: #212529; flex: 1;">${this.escapeHtml(message)}</span>
                    </div>
                    <div style="display: flex; gap: 12px; justify-content: flex-end;">
                        ${cancelText ? `<button class="oriola-confirm-btn oriola-confirm-cancel" style="
                            padding: 10px 20px;
                            border: 1px solid #dee2e6;
                            background: #ffffff;
                            color: #6c757d;
                            border-radius: 6px;
                            cursor: pointer;
                            font-size: 14px;
                            font-weight: 500;
                            transition: all 0.2s;
                        ">${this.escapeHtml(cancelText)}</button>` : ''}
                        <button class="oriola-confirm-btn oriola-confirm-ok" style="
                            padding: 10px 20px;
                            border: 1px solid #ffc107;
                            background: #ffc107;
                            color: #212529;
                            border-radius: 6px;
                            cursor: pointer;
                            font-size: 14px;
                            font-weight: 600;
                            transition: all 0.2s;
                        ">${this.escapeHtml(confirmText)}</button>
                    </div>
                </div>
            `;

            // Agregar estilos de hover
            const style = document.createElement('style');
            style.textContent = `
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
                @keyframes slideDown {
                    from { 
                        opacity: 0;
                        transform: translateY(-20px) scale(0.95);
                    }
                    to { 
                        opacity: 1;
                        transform: translateY(0) scale(1);
                    }
                }
                .oriola-confirm-btn:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
                }
                .oriola-confirm-cancel:hover {
                    background: #f8f9fa !important;
                    border-color: #adb5bd !important;
                }
                .oriola-confirm-ok:hover {
                    background: #ffb300 !important;
                    border-color: #ffb300 !important;
                }
                .oriola-confirm-btn:active {
                    transform: translateY(0);
                }
            `;
            document.head.appendChild(style);

            // Funciones de limpieza
            const cleanup = () => {
                overlay.style.animation = 'fadeOut 0.2s ease-in-out';
                dialog.style.animation = 'slideUp 0.2s ease-out';
                setTimeout(() => {
                    if (overlay.parentNode) {
                        overlay.remove();
                    }
                    if (style.parentNode) {
                        style.remove();
                    }
                }, 200);
            };

            // Botón cancelar (solo si existe)
            const cancelBtn = dialog.querySelector('.oriola-confirm-cancel');
            if (cancelBtn) {
                cancelBtn.addEventListener('click', () => {
                    cleanup();
                    resolve(false);
                });
            }

            // Botón aceptar
            dialog.querySelector('.oriola-confirm-ok').addEventListener('click', () => {
                cleanup();
                resolve(true);
            });

            // Cerrar al hacer clic en el overlay
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) {
                    cleanup();
                    resolve(false);
                }
            });

            // Agregar al DOM
            overlay.appendChild(dialog);
            document.body.appendChild(overlay);

            // Agregar animación de salida
            const fadeOutStyle = document.createElement('style');
            fadeOutStyle.textContent = `
                @keyframes fadeOut {
                    from { opacity: 1; }
                    to { opacity: 0; }
                }
                @keyframes slideUp {
                    from { 
                        opacity: 1;
                        transform: translateY(0) scale(1);
                    }
                    to { 
                        opacity: 0;
                        transform: translateY(-20px) scale(0.95);
                    }
                }
            `;
            document.head.appendChild(fadeOutStyle);
        });
    }
}

// Crear instancia global
const oriolaMessages = new OriolaMessages();

// Función global para compatibilidad con código existente
function showOriolaMessage(type, message, duration) {
    switch(type) {
        case 'error':
        case 'danger':
            oriolaMessages.showError(message, duration);
            break;
        case 'success':
            oriolaMessages.showSuccess(message, duration);
            break;
        case 'info':
            oriolaMessages.showInfo(message, duration);
            break;
        case 'warning':
            oriolaMessages.showWarning(message, duration);
            break;
        default:
            oriolaMessages.showInfo(message, duration);
    }
}

// Reemplazar alert() nativo (opcional, para compatibilidad)
const originalAlert = window.alert;
window.alert = function(message) {
    // Detectar si es un error o éxito por el contenido
    if (message.includes('❌') || message.toLowerCase().includes('error')) {
        oriolaMessages.showError(message.replace('❌', '').trim());
    } else if (message.includes('✅') || message.toLowerCase().includes('éxito') || message.toLowerCase().includes('exitoso')) {
        oriolaMessages.showSuccess(message.replace('✅', '').trim());
    } else {
        oriolaMessages.showInfo(message);
    }
};

// Reemplazar confirm() nativo con el sistema Oriola
const originalConfirm = window.confirm;
window.confirm = async function(message) {
    return await oriolaMessages.confirm(message);
};

