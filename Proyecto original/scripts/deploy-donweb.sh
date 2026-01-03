#!/bin/bash
# deploy-donweb.sh - Script de despliegue automático para OriolaIndumentaria
# Servidor: Donweb - 66.97.45.252 (NUEVO)

set -e  # Salir si hay algún error

echo "🚀 =========================================="
echo "   ORIOLA INDUMENTARIA - DESPLIEGUE DONWEB"
echo "🚀 =========================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir con colores
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    print_error "No se encontró pom.xml. Asegúrate de estar en el directorio del proyecto."
    exit 1
fi

# 1. Parar aplicación actual
print_status "1️⃣ Parando aplicación actual..."
if pgrep -f "oriola-denim-0.0.1-SNAPSHOT.jar" > /dev/null; then
    print_status "Aplicación encontrada, deteniendo..."
    pkill -f "oriola-denim-0.0.1-SNAPSHOT.jar"
    sleep 3
    
    # Verificar que se haya detenido
    if pgrep -f "oriola-denim-0.0.1-SNAPSHOT.jar" > /dev/null; then
        print_warning "La aplicación aún está corriendo, forzando cierre..."
        pkill -9 -f "oriola-denim-0.0.1-SNAPSHOT.jar"
        sleep 2
    fi
    print_success "Aplicación detenida correctamente"
else
    print_warning "No se encontró aplicación corriendo"
fi

# 2. Hacer pull de cambios
print_status "2️⃣ Actualizando código desde GitHub..."
git fetch origin
git pull origin master
print_success "Código actualizado desde GitHub"

# 3. Compilar aplicación
print_status "3️⃣ Compilando aplicación..."
print_status "Esto puede tomar unos minutos..."

if mvn clean package -DskipTests; then
    print_success "Compilación exitosa"
else
    print_error "Error en la compilación"
    exit 1
fi

# 4. Verificar que el JAR se creó
if [ ! -f "target/oriola-denim-0.0.1-SNAPSHOT.jar" ]; then
    print_error "No se encontró el archivo JAR compilado"
    exit 1
fi

# 5. Ejecutar en segundo plano
print_status "4️⃣ Iniciando aplicación en segundo plano..."
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# 6. Esperar un momento para que inicie
print_status "Esperando que la aplicación inicie..."
sleep 10

# 7. Verificar que esté corriendo
print_status "5️⃣ Verificando estado de la aplicación..."
if pgrep -f "oriola-denim-0.0.1-SNAPSHOT.jar" > /dev/null; then
    PID=$(pgrep -f "oriola-denim-0.0.1-SNAPSHOT.jar")
    print_success "✅ Aplicación iniciada correctamente (PID: $PID)"
    echo ""
    echo "🌐 =========================================="
    echo "   APLICACIÓN DISPONIBLE EN:"
    echo "   http://localhost:8080"
    echo "   http://66.97.45.252:8080"
    echo "🌐 =========================================="
    echo ""
    print_status "Para ver los logs: tail -f app.log"
    print_status "Para parar la aplicación: pkill -f 'oriola-denim-0.0.1-SNAPSHOT.jar'"
else
    print_error "❌ Error: La aplicación no se inició correctamente"
    print_status "Revisa los logs: cat app.log"
    exit 1
fi

echo ""
print_success "🎉 DESPLIEGUE COMPLETADO EXITOSAMENTE"
echo ""

