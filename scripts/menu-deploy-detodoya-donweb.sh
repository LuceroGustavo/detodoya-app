#!/bin/bash
# menu-deploy-detodoya-donweb.sh - Menú interactivo para gestión de Detodoya.com
# Servidor: Donweb - 149.50.144.53
# Puerto: 8080 (Fulbito usa 8081, NO TOCAR)

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Función para imprimir con colores
print_header() {
    echo -e "${CYAN}=========================================="
    echo -e "   DETODOYA.COM - MENÚ DE GESTIÓN"
    echo -e "==========================================${NC}"
}

print_option() {
    echo -e "${BLUE}$1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Función para verificar si la aplicación está corriendo
check_app_status() {
    if pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
        PID=$(pgrep -f "detodoya-0.0.1-SNAPSHOT.jar")
        echo -e "${GREEN}🟢 Detodoya corriendo (PID: $PID) - Puerto 8080${NC}"
        return 0
    else
        echo -e "${RED}🔴 Detodoya no está corriendo${NC}"
        return 1
    fi
}

# Función para verificar Fulbito (NO TOCAR)
check_fulbito_status() {
    if netstat -tlnp | grep -q ":8081 "; then
        echo -e "${GREEN}🟢 Fulbito corriendo en puerto 8081 (NO TOCAR)${NC}"
    else
        echo -e "${YELLOW}🟡 Fulbito no detectado en puerto 8081${NC}"
    fi
}

# Función para parar la aplicación
stop_app() {
    print_option "1️⃣ Parando aplicación Detodoya..."
    if pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
        pkill -f "detodoya-0.0.1-SNAPSHOT.jar"
        sleep 3
        
        if pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
            print_warning "Forzando cierre..."
            pkill -9 -f "detodoya-0.0.1-SNAPSHOT.jar"
        fi
        print_success "Aplicación Detodoya detenida"
    else
        print_warning "No hay aplicación Detodoya corriendo"
    fi
    echo ""
    check_fulbito_status
}

# Función para actualizar código
update_code() {
    print_option "2️⃣ Actualizando código desde GitHub..."
    # Obtener el directorio del script y usarlo como base
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    cd "$SCRIPT_DIR" || { print_error "Error: No se pudo cambiar al directorio del proyecto"; return 1; }
    git fetch origin
    # Intentar con main primero, luego master como fallback
    if git rev-parse --verify origin/main > /dev/null 2>&1; then
        if git pull origin main; then
            print_success "Código actualizado desde main"
        else
            print_error "Error al actualizar código desde main"
        fi
    else
        if git pull origin master; then
            print_success "Código actualizado desde master"
        else
            print_error "Error al actualizar código"
        fi
    fi
}

# Función para compilar
compile_app() {
    print_option "3️⃣ Compilando aplicación Detodoya..."
    print_warning "Esto puede tomar varios minutos..."
    # Obtener el directorio del script y usarlo como base
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    cd "$SCRIPT_DIR" || { print_error "Error: No se pudo cambiar al directorio del proyecto"; return 1; }
    if mvn clean package -DskipTests; then
        print_success "Compilación exitosa"
    else
        print_error "Error en la compilación"
    fi
}

# Función para iniciar aplicación
start_app() {
    print_option "4️⃣ Iniciando aplicación Detodoya..."
    # Obtener el directorio del script y usarlo como base
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    cd "$SCRIPT_DIR" || { print_error "Error: No se pudo cambiar al directorio del proyecto"; return 1; }
    if [ ! -f "target/detodoya-0.0.1-SNAPSHOT.jar" ]; then
        print_error "No se encontró el JAR. Compila primero (opción 3)"
        return 1
    fi
    
    # Verificar que el puerto 8080 esté disponible
    if netstat -tlnp | grep -q ":8080 " && ! pgrep -f "detodoya-0.0.1-SNAPSHOT.jar" > /dev/null; then
        print_error "Puerto 8080 está ocupado por otra aplicación"
        netstat -tlnp | grep ":8080 "
        return 1
    fi
    
    nohup java -jar target/detodoya-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb > app.log 2>&1 &
    sleep 5
    
    if check_app_status; then
        print_success "Aplicación Detodoya iniciada correctamente"
        echo -e "${CYAN}🌐 Disponible en: http://149.50.144.53:8080${NC}"
        echo -e "${CYAN}🌐 Disponible en: http://detodoya.com:8080${NC}"
    else
        print_error "Error al iniciar la aplicación"
        print_warning "Revisa los logs: tail -f app.log"
    fi
    echo ""
    check_fulbito_status
}

# Función para despliegue completo
full_deploy() {
    print_option "5️⃣ Despliegue completo iniciado..."
    stop_app
    update_code
    compile_app
    start_app
    echo ""
    print_success "🎉 Despliegue completo de Detodoya finalizado"
}

# Función para ver logs
view_logs() {
    print_option "7️⃣ Mostrando logs de la aplicación Detodoya..."
    # Obtener el directorio del script y usarlo como base
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    cd "$SCRIPT_DIR" || { print_error "Error: No se pudo cambiar al directorio del proyecto"; return 1; }
    if [ -f "app.log" ]; then
        echo -e "${YELLOW}Últimas 50 líneas de logs:${NC}"
        tail -50 app.log
        echo ""
        echo -e "${BLUE}Para ver logs en tiempo real: tail -f app.log${NC}"
    elif [ -f "nohup.out" ]; then
        echo -e "${YELLOW}Últimas 50 líneas de logs:${NC}"
        tail -50 nohup.out
        echo ""
        echo -e "${BLUE}Para ver logs en tiempo real: tail -f nohup.out${NC}"
    else
        print_warning "No se encontró archivo de logs"
    fi
}

# Función para ver estado del sistema
system_status() {
    print_option "6️⃣ Estado del sistema:"
    echo ""
    echo -e "${CYAN}=== APLICACIÓN DETODOYA ===${NC}"
    check_app_status
    echo ""
    echo -e "${CYAN}=== APLICACIÓN FULBITO (NO TOCAR) ===${NC}"
    check_fulbito_status
    echo ""
    echo -e "${CYAN}=== RECURSOS DEL SERVIDOR ===${NC}"
    echo "Uso de CPU y memoria:"
    top -bn1 | head -5
    echo ""
    echo -e "${CYAN}=== ESPACIO EN DISCO ===${NC}"
    df -h | grep -E "(Filesystem|/dev/)"
    echo ""
    echo -e "${CYAN}=== MEMORIA ===${NC}"
    free -h
    echo ""
    echo -e "${CYAN}=== PUERTOS EN USO ===${NC}"
    echo "Puerto 8080 (Detodoya):"
    netstat -tlnp | grep ":8080 " || echo "  No hay aplicación en puerto 8080"
    echo "Puerto 8081 (Fulbito - NO TOCAR):"
    netstat -tlnp | grep ":8081 " || echo "  No hay aplicación en puerto 8081"
}

# Función para reiniciar aplicación
restart_app() {
    print_option "8️⃣ Reiniciando aplicación Detodoya..."
    stop_app
    sleep 2
    start_app
}

# Función para ver información del proyecto
project_info() {
    print_option "9️⃣ Información del proyecto Detodoya:"
    # Obtener el directorio del script y usarlo como base
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    cd "$SCRIPT_DIR" || { print_error "Error: No se pudo cambiar al directorio del proyecto"; return 1; }
    echo ""
    echo -e "${CYAN}=== INFORMACIÓN GIT ===${NC}"
    echo "Rama actual: $(git branch --show-current)"
    echo "Último commit: $(git log -1 --oneline)"
    echo "Estado: $(git status --porcelain | wc -l) archivos modificados"
    echo ""
    echo -e "${CYAN}=== INFORMACIÓN DEL PROYECTO ===${NC}"
    echo "Directorio: $(pwd)"
    echo "JAR disponible: $([ -f "target/detodoya-0.0.1-SNAPSHOT.jar" ] && echo "Sí" || echo "No")"
    echo "Tamaño del JAR: $([ -f "target/detodoya-0.0.1-SNAPSHOT.jar" ] && du -h target/detodoya-0.0.1-SNAPSHOT.jar | cut -f1 || echo "N/A")"
    echo ""
    echo -e "${CYAN}=== CONFIGURACIÓN ===${NC}"
    echo "Servidor: Donweb"
    echo "IP: 149.50.144.53"
    echo "Puerto: 8080"
    echo "Perfil: donweb"
    echo "Base de datos: detodoya"
    echo ""
    echo -e "${CYAN}=== APLICACIONES EN EL SERVIDOR ===${NC}"
    check_app_status
    check_fulbito_status
}

# Función para ver espacio en disco
disk_space() {
    print_option "🔟 Espacio en disco:"
    echo ""
    
    # Espacio total del sistema
    echo -e "${CYAN}=== ESPACIO TOTAL DEL SERVIDOR ===${NC}"
    df -h / | awk 'NR==1 || NR==2 {print}'
    
    # Obtener porcentaje de uso
    USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
    AVAILABLE=$(df -h / | tail -1 | awk '{print $4}')
    USED=$(df -h / | tail -1 | awk '{print $3}')
    TOTAL=$(df -h / | tail -1 | awk '{print $2}')
    
    echo ""
    echo -e "${BLUE}📊 Resumen:${NC}"
    echo "  Total: $TOTAL"
    echo "  Usado: $USED"
    echo "  Disponible: $AVAILABLE"
    echo "  Uso: ${USAGE}%"
    
    # Advertencias según el uso
    if [ "$USAGE" -ge 90 ]; then
        echo ""
        print_error "⚠️  CRÍTICO: Espacio en disco muy bajo (${USAGE}%)"
        print_warning "Considera limpiar archivos o aumentar el almacenamiento"
    elif [ "$USAGE" -ge 80 ]; then
        echo ""
        print_warning "⚠️  Advertencia: Espacio en disco bajo (${USAGE}%)"
        print_warning "Considera hacer limpieza de archivos antiguos"
    elif [ "$USAGE" -ge 70 ]; then
        echo ""
        print_warning "ℹ️  Espacio en disco moderado (${USAGE}%)"
    else
        echo ""
        print_success "✅ Espacio en disco saludable (${USAGE}%)"
    fi
    
    echo ""
    echo -e "${CYAN}=== ESPACIO POR DIRECTORIO ===${NC}"
    
    # Espacio usado por uploads
    if [ -d "/home/detodoya/uploads" ]; then
        UPLOADS_SIZE=$(du -sh /home/detodoya/uploads 2>/dev/null | cut -f1)
        UPLOADS_FILES=$(find /home/detodoya/uploads -type f 2>/dev/null | wc -l)
        echo "📁 Uploads (/home/detodoya/uploads):"
        echo "   Tamaño: ${UPLOADS_SIZE:-0}"
        echo "   Archivos: ${UPLOADS_FILES:-0}"
    else
        echo "📁 Uploads: Directorio no existe"
    fi
    
    # Espacio usado por backups
    if [ -d "/home/detodoya/backups" ]; then
        BACKUPS_SIZE=$(du -sh /home/detodoya/backups 2>/dev/null | cut -f1)
        BACKUPS_FILES=$(find /home/detodoya/backups -type f 2>/dev/null | wc -l)
        echo "💾 Backups (/home/detodoya/backups):"
        echo "   Tamaño: ${BACKUPS_SIZE:-0}"
        echo "   Archivos: ${BACKUPS_FILES:-0}"
    else
        echo "💾 Backups: Directorio no existe"
    fi
    
    # Espacio usado por el proyecto
    if [ -d "/home/detodoya/Detodoya.com" ]; then
        PROJECT_SIZE=$(du -sh /home/detodoya/Detodoya.com 2>/dev/null | cut -f1)
        echo "📦 Proyecto Detodoya (/home/detodoya/Detodoya.com):"
        echo "   Tamaño: ${PROJECT_SIZE:-0}"
    else
        echo "📦 Proyecto: Directorio no existe"
    fi
    
    echo ""
    echo -e "${CYAN}=== TOP 10 DIRECTORIOS MÁS GRANDES ===${NC}"
    du -h --max-depth=1 /home/detodoya 2>/dev/null | sort -hr | head -10 | awk '{printf "  %-50s %s\n", $2, $1}'
}

# Menú principal
while true; do
    clear
    print_header
    echo ""
    check_app_status
    echo ""
    check_fulbito_status
    echo ""
    print_option "Selecciona una opción:"
    echo ""
    echo "1.  Parar aplicación Detodoya"
    echo "2.  Actualizar código (git pull)"
    echo "3.  Compilar aplicación Detodoya"
    echo "4.  Iniciar aplicación Detodoya"
    echo "5.  Despliegue completo (1+2+3+4)"
    echo "6.  Ver estado del sistema"
    echo "7.  Ver logs de la aplicación"
    echo "8.  Reiniciar aplicación Detodoya"
    echo "9.  Información del proyecto"
    echo "10. Ver espacio en disco"
    echo "11. Salir"
    echo ""
    read -p "Ingresa tu opción (1-11): " opcion
    echo ""
    
    case $opcion in
        1) stop_app ;;
        2) update_code ;;
        3) compile_app ;;
        4) start_app ;;
        5) full_deploy ;;
        6) system_status ;;
        7) view_logs ;;
        8) restart_app ;;
        9) project_info ;;
        10) disk_space ;;
        11) 
            print_success "Saliendo del menú..."
            exit 0 
            ;;
        *) 
            print_error "Opción inválida. Presiona Enter para continuar..."
            read
            ;;
    esac
    
    echo ""
    read -p "Presiona Enter para continuar..."
done

