#!/bin/bash
# deploy-detodoya-donweb.sh - Script de despliegue automático para Detodoya.com
# Servidor: Donweb - 149.50.144.53
# Puerto: 8080 (Fulbito usa 8081, NO TOCAR)

set -e  # Salir si hay algún error

echo "🚀 =========================================="
echo "   DETODOYA.COM - DESPLIEGUE DONWEB"
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

# Verificar que el puerto 8080 no esté ocupado por otra aplicación (excepto Detodoya)
print_status "Verificando puerto 8080..."
if netstat -tlnp | grep -q ":8080 " && ! pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
    print_warning "Puerto 8080 está ocupado por otra aplicación. Verificando..."
    netstat -tlnp | grep ":8080 "
    print_error "Por favor, verifica qué aplicación está usando el puerto 8080"
    exit 1
fi

# 1. Parar aplicación actual de Detodoya (si existe)
print_status "1️⃣ Parando aplicación Detodoya actual (si existe)..."
if pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
    print_status "Aplicación Detodoya encontrada, deteniendo..."
    pkill -f "detodoya-0.0.1-SNAPSHOT.jar"
    sleep 3
    
    # Verificar que se haya detenido
    if pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
        print_warning "La aplicación aún está corriendo, forzando cierre..."
        pkill -9 -f "detodoya-0.0.1-SNAPSHOT.jar"
        sleep 2
    fi
    print_success "Aplicación Detodoya detenida correctamente"
else
    print_warning "No se encontró aplicación Detodoya corriendo"
fi

# Verificar que Fulbito sigue funcionando en 8081 (NO TOCAR)
print_status "Verificando que Fulbito sigue funcionando en puerto 8081..."
if netstat -tlnp | grep -q ":8081 "; then
    print_success "✅ Fulbito está corriendo en puerto 8081 (correcto, no se toca)"
else
    print_warning "⚠️  Fulbito no está corriendo en puerto 8081 (puede ser normal si no está activo)"
fi

# 2. Hacer pull de cambios
print_status "2️⃣ Actualizando código desde GitHub..."
git fetch origin
# Intentar con main primero, luego master como fallback
if git rev-parse --verify origin/main > /dev/null 2>&1; then
    git pull origin main
else
    git pull origin master
fi
print_success "Código actualizado desde GitHub"

# 3. Compilar aplicación
print_status "3️⃣ Compilando aplicación Detodoya..."
print_status "Esto puede tomar unos minutos..."

if mvn clean package -DskipTests; then
    print_success "Compilación exitosa"
else
    print_error "Error en la compilación"
    exit 1
fi

# 4. Verificar que el JAR se creó
if [ ! -f "target/detodoya-0.0.1-SNAPSHOT.jar" ]; then
    print_error "No se encontró el archivo JAR compilado (detodoya-0.0.1-SNAPSHOT.jar)"
    exit 1
fi

# 5. Ejecutar en segundo plano
print_status "4️⃣ Iniciando aplicación Detodoya en segundo plano (puerto 8080)..."
nohup java -jar target/detodoya-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &

# 6. Esperar un momento para que inicie
print_status "Esperando que la aplicación inicie..."
sleep 10

# 7. Verificar que esté corriendo
print_status "5️⃣ Verificando estado de la aplicación..."
if pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
    PID=$(pgrep -f "detodoya-0.0.1-SNAPSHOT.jar")
    print_success "✅ Aplicación Detodoya iniciada correctamente (PID: $PID)"
    echo ""
    echo "🌐 =========================================="
    echo "   APLICACIÓN DETODOYA DISPONIBLE EN:"
    echo "   http://149.50.144.53:8080"
    echo "   http://detodoya.com:8080"
    echo "🌐 =========================================="
    echo ""
    print_status "Para ver los logs: tail -f app.log"
    print_status "Para parar la aplicación: pkill -f 'detodoya-0.0.1-SNAPSHOT.jar'"
    print_status "⚠️  RECORDATORIO: Fulbito está en puerto 8081 (NO TOCAR)"
else
    print_error "❌ Error: La aplicación Detodoya no se inició correctamente"
    print_status "Revisa los logs: cat app.log"
    exit 1
fi

echo ""
print_success "🎉 DESPLIEGUE DE DETODOYA COMPLETADO EXITOSAMENTE"
echo ""

