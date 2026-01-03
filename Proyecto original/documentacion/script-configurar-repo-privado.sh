#!/bin/bash
# script-configurar-repo-privado.sh
# Script para configurar repositorio privado en el servidor

set -e

echo "🔒 =========================================="
echo "   CONFIGURACIÓN DE REPOSITORIO PRIVADO"
echo "🔒 =========================================="
echo ""

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Verificar que estamos en el directorio correcto
if [ ! -d "/home/oriola/OriolaIndumentaria" ]; then
    print_error "El directorio /home/oriola/OriolaIndumentaria no existe"
    exit 1
fi

cd /home/oriola/OriolaIndumentaria

# Verificar URL actual
print_status "URL actual del repositorio:"
git remote -v
echo ""

# Opción 1: Usar Personal Access Token (PAT)
echo "¿Qué método quieres usar?"
echo "1) Personal Access Token (PAT) - Más fácil"
echo "2) SSH Keys - Más seguro"
read -p "Elige una opción (1 o 2): " opcion

if [ "$opcion" = "1" ]; then
    print_status "Configurando con Personal Access Token..."
    read -p "Ingresa tu Personal Access Token (ghp_...): " token
    
    if [ -z "$token" ]; then
        print_error "El token no puede estar vacío"
        exit 1
    fi
    
    # Configurar URL con token
    git remote set-url origin "https://${token}@github.com/LuceroGustavo/OriolaIndumentaria.git"
    print_success "URL del repositorio actualizada con token"
    
    # Verificar que funciona
    print_status "Verificando conexión..."
    if git fetch origin 2>&1 | grep -q "fatal"; then
        print_error "No se pudo conectar al repositorio. Verifica el token."
        exit 1
    else
        print_success "✅ Conexión exitosa al repositorio privado"
    fi
    
elif [ "$opcion" = "2" ]; then
    print_status "Configurando con SSH Keys..."
    
    # Verificar si existe SSH key
    if [ ! -f ~/.ssh/id_ed25519.pub ] && [ ! -f ~/.ssh/id_rsa.pub ]; then
        print_warning "No se encontró SSH key. Generando una nueva..."
        ssh-keygen -t ed25519 -C "oriola-server@66.97.45.252" -f ~/.ssh/id_ed25519 -N ""
        print_success "SSH key generada"
    fi
    
    # Mostrar clave pública
    if [ -f ~/.ssh/id_ed25519.pub ]; then
        KEY_FILE=~/.ssh/id_ed25519.pub
    else
        KEY_FILE=~/.ssh/id_rsa.pub
    fi
    
    print_status "Tu clave pública SSH es:"
    echo ""
    cat "$KEY_FILE"
    echo ""
    print_warning "⚠️ IMPORTANTE: Copia la clave de arriba y agrégala a GitHub:"
    print_warning "   https://github.com/settings/keys"
    echo ""
    read -p "Presiona Enter cuando hayas agregado la clave a GitHub..."
    
    # Cambiar URL a SSH
    git remote set-url origin git@github.com:LuceroGustavo/OriolaIndumentaria.git
    print_success "URL del repositorio actualizada a SSH"
    
    # Verificar que funciona
    print_status "Verificando conexión..."
    if git fetch origin 2>&1 | grep -q "fatal"; then
        print_error "No se pudo conectar al repositorio. Verifica que agregaste la SSH key a GitHub."
        exit 1
    else
        print_success "✅ Conexión exitosa al repositorio privado"
    fi
    
else
    print_error "Opción inválida"
    exit 1
fi

# Verificar estado final
echo ""
print_status "Configuración final:"
git remote -v
echo ""

print_success "🎉 Repositorio privado configurado correctamente"
print_status "Puedes usar 'git pull' y 'git fetch' normalmente ahora"

