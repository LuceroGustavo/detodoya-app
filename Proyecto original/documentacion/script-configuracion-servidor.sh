#!/bin/bash
# Script de Configuración Automática - Servidor Donweb ORIOLA
# Fecha: 29 de diciembre de 2025
# Servidor: 66.97.45.252

set -e  # Salir si hay algún error

echo "🚀 =========================================="
echo "   CONFIGURACIÓN SERVIDOR ORIOLA - DONWEB"
echo "🚀 =========================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# ==========================================
# PASO 1: VERIFICAR SISTEMA
# ==========================================
print_status "1️⃣ Verificando sistema..."

echo "Sistema Operativo:"
cat /etc/os-release | grep PRETTY_NAME

echo ""
echo "Recursos del servidor:"
free -h
df -h /

echo ""
print_status "Verificando Git..."
if git --version > /dev/null 2>&1; then
    GIT_VERSION=$(git --version)
    print_success "Git instalado: $GIT_VERSION"
else
    print_warning "Git no encontrado, se instalará"
fi

echo ""
read -p "Presiona Enter para continuar con la actualización del sistema..."

# ==========================================
# PASO 2: ACTUALIZAR SISTEMA
# ==========================================
print_status "2️⃣ Actualizando sistema..."
print_warning "Esto puede tomar varios minutos..."

sudo apt update
sudo apt upgrade -y

print_success "Sistema actualizado"

# ==========================================
# PASO 3: INSTALAR JAVA 17
# ==========================================
print_status "3️⃣ Instalando Java 17 (OpenJDK)..."

sudo apt install openjdk-17-jdk -y

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
print_success "Java instalado: $JAVA_VERSION"

# ==========================================
# PASO 4: INSTALAR MYSQL 8.0
# ==========================================
print_status "4️⃣ Instalando MySQL 8.0..."

sudo apt install mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql

MYSQL_VERSION=$(mysql --version)
print_success "MySQL instalado: $MYSQL_VERSION"

print_warning "IMPORTANTE: Necesitarás configurar MySQL manualmente"
echo "Ejecuta: sudo mysql_secure_installation"
echo "Contraseña root MySQL: OriolaMySQL2025!"

# ==========================================
# PASO 5: INSTALAR MAVEN
# ==========================================
print_status "5️⃣ Instalando Maven..."

sudo apt install maven -y

MAVEN_VERSION=$(mvn -version | head -n 1)
print_success "Maven instalado: $MAVEN_VERSION"

# ==========================================
# PASO 6: INSTALAR NGINX
# ==========================================
print_status "6️⃣ Instalando Nginx..."

sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx

NGINX_VERSION=$(nginx -v 2>&1)
print_success "Nginx instalado: $NGINX_VERSION"

# ==========================================
# PASO 7: CONFIGURAR FIREWALL UFW
# ==========================================
print_status "7️⃣ Configurando firewall UFW..."

sudo apt install ufw -y

# Permitir SSH (puerto 5625)
sudo ufw allow 5625/tcp

# Permitir HTTP y HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Permitir puerto de la aplicación
sudo ufw allow 8080/tcp

# Habilitar firewall
echo "y" | sudo ufw enable

print_success "Firewall UFW configurado"
print_warning "⚠️ RECUERDA: También debes configurar el firewall en el panel de Donweb"

# ==========================================
# PASO 8: CREAR DIRECTORIOS
# ==========================================
print_status "8️⃣ Creando directorios para la aplicación..."

sudo mkdir -p /home/oriola/uploads
sudo mkdir -p /home/oriola/uploads/thumbnails
sudo mkdir -p /home/oriola/backups

sudo chown -R $USER:$USER /home/oriola
sudo chmod -R 755 /home/oriola

print_success "Directorios creados"

# ==========================================
# PASO 9: CONFIGURAR BASE DE DATOS
# ==========================================
print_status "9️⃣ Configurando base de datos MySQL..."

print_warning "Ejecutando comandos SQL para crear base de datos y usuario..."

sudo mysql <<EOF
CREATE DATABASE IF NOT EXISTS orioladenim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'oriola_user'@'localhost' IDENTIFIED BY 'OriolaDB2025!';
GRANT ALL PRIVILEGES ON orioladenim.* TO 'oriola_user'@'localhost';
FLUSH PRIVILEGES;
SHOW DATABASES;
SELECT user, host FROM mysql.user WHERE user = 'oriola_user';
EOF

print_success "Base de datos configurada"

# ==========================================
# PASO 10: CLONAR REPOSITORIO
# ==========================================
print_status "🔟 Clonando repositorio desde GitHub..."

cd /home/oriola

if [ -d "OriolaIndumentaria" ]; then
    print_warning "El directorio OriolaIndumentaria ya existe"
    read -p "¿Deseas eliminarlo y clonar de nuevo? (s/n): " respuesta
    if [ "$respuesta" = "s" ] || [ "$respuesta" = "S" ]; then
        rm -rf OriolaIndumentaria
        git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git
        print_success "Repositorio clonado"
    else
        print_status "Usando repositorio existente"
        cd OriolaIndumentaria
        git pull origin master
    fi
else
    git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git
    print_success "Repositorio clonado"
fi

cd OriolaIndumentaria

# ==========================================
# PASO 11: VERIFICAR/CREAR application-donweb.properties
# ==========================================
print_status "1️⃣1️⃣ Verificando configuración de aplicación..."

if [ ! -f "src/main/resources/application-donweb.properties" ]; then
    print_warning "Creando application-donweb.properties..."
    
    cat > src/main/resources/application-donweb.properties <<'PROPERTIES'
# ===========================================
# CONFIGURACIÓN PARA DONWEB
# Servidor: 66.97.45.252 - Buenos Aires, Argentina
# ===========================================

# Puerto del servidor
server.port=8080
server.address=0.0.0.0
server.servlet.context-path=/

# ===========================================
# BASE DE DATOS MYSQL (DONWEB)
# ===========================================
spring.datasource.url=jdbc:mysql://localhost:3306/orioladenim?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=oriola_user
spring.datasource.password=OriolaDB2025!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===========================================
# CONFIGURACIÓN JPA/HIBERNATE
# ===========================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# ===========================================
# CONFIGURACIÓN DE ARCHIVOS (PERSISTENTE)
# ===========================================
file.upload-dir=/home/oriola/uploads
backup.directory=/home/oriola/backups

# Configuración de archivos estáticos
spring.web.resources.static-locations=classpath:/static/,file:/home/oriola/uploads/
upload.path=/home/oriola/uploads
upload.thumbnail.path=/home/oriola/uploads/thumbnails

# ===========================================
# CONFIGURACIÓN DE SUBIDA DE ARCHIVOS
# ===========================================
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=200MB

# ===========================================
# CONFIGURACIÓN DE EMAIL
# ===========================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:luceroprograma@gmail.com}
spring.mail.password=${MAIL_PASSWORD:kmqh ktkl lhyj gwlf}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# ===========================================
# CONFIGURACIÓN DE SEGURIDAD
# ===========================================
spring.security.user.name=admin
spring.security.user.password=${ADMIN_PASSWORD:OriolaAdmin2025!}
spring.security.user.roles=ADMIN

# ===========================================
# CONFIGURACIÓN DE LOGGING
# ===========================================
logging.level.com.orioladenim=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN

# ===========================================
# CONFIGURACIÓN DE JACKSON (FECHAS)
# ===========================================
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.deserialization.fail-on-unknown-properties=false
PROPERTIES

    print_success "Archivo application-donweb.properties creado"
else
    print_status "Verificando que server.address=0.0.0.0 está configurado..."
    if ! grep -q "server.address=0.0.0.0" src/main/resources/application-donweb.properties; then
        echo "server.address=0.0.0.0" >> src/main/resources/application-donweb.properties
        print_success "server.address=0.0.0.0 agregado"
    else
        print_success "server.address=0.0.0.0 ya está configurado"
    fi
fi

# ==========================================
# RESUMEN FINAL
# ==========================================
echo ""
echo "=========================================="
print_success "🎉 CONFIGURACIÓN COMPLETADA"
echo "=========================================="
echo ""
echo "✅ Software instalado:"
echo "   - Java 17"
echo "   - MySQL 8.0"
echo "   - Maven"
echo "   - Nginx"
echo ""
echo "✅ Configuración completada:"
echo "   - Firewall UFW configurado"
echo "   - Base de datos creada"
echo "   - Directorios creados"
echo "   - Repositorio clonado"
echo ""
print_warning "⚠️ PENDIENTE (MANUAL):"
echo "   1. Configurar firewall Donweb en el panel"
echo "   2. Ejecutar: sudo mysql_secure_installation"
echo "   3. Compilar aplicación: mvn clean package -DskipTests"
echo "   4. Ejecutar aplicación"
echo ""
echo "📋 Próximos pasos:"
echo "   1. cd /home/oriola/OriolaIndumentaria"
echo "   2. mvn clean package -DskipTests"
echo "   3. java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=donweb"
echo ""

