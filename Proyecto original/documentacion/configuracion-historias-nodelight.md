# Configuración de Historias en Servidor NodeLight - ORIOLA Indumentaria

## 🎯 **RESUMEN**
Esta documentación explica cómo configurar el sistema de historias tipo Instagram en el servidor NodeLight para el proyecto ORIOLA Indumentaria.

## 📋 **FUNCIONALIDADES IMPLEMENTADAS**

### **✅ Backend Completo:**
- **Entidad Historia**: Campos para video, thumbnail, título, descripción
- **Repositorio**: Consultas optimizadas para historias activas
- **Servicio**: Lógica de negocio completa con procesamiento de videos
- **Controlador**: Administración completa de historias
- **Procesamiento de Videos**: Validación, conversión y thumbnails

### **✅ Frontend Completo:**
- **Panel de Administración**: Gestión completa de historias
- **Página Principal**: Historia principal con video promocional
- **Diseño Responsive**: Optimizado para móvil y desktop
- **Controles de Video**: Play/pause automático

## 🚀 **CONFIGURACIÓN EN SERVIDOR NODELIGHT**

### **1. Variables de Entorno Requeridas:**

```bash
# Perfil de Spring Boot
SPRING_PROFILES_ACTIVE=prod

# Base de datos (configurada localmente)
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/oriola_indumentaria
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=[configurada_localmente]

# Administración
ADMIN_USERNAME=admin
ADMIN_PASSWORD=tu_contraseña_segura

# Email (opcional)
MAIL_USERNAME=luceroprograma@gmail.com
MAIL_PASSWORD=kmqh ktkl lhyj gwlf
```

### **2. Archivos de Configuración:**

#### **application-prod.properties**
```properties
# Configuración específica para servidor NodeLight
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/oriola_indumentaria
upload.path=/var/www/oriola/uploads
upload.thumbnail.path=/var/www/oriola/uploads/thumbnails
spring.servlet.multipart.max-file-size=10MB
```

## 📁 **ESTRUCTURA DE ARCHIVOS EN SERVIDOR NODELIGHT**

```
/var/www/oriola/
├── uploads/
│   ├── historias/          # Videos de historias
│   └── thumbnails/
│       └── historias/      # Thumbnails de videos
├── backups/               # Backups de base de datos
└── target/
    └── oriola-denim-0.0.1-SNAPSHOT.jar
```

## 🎬 **ESPECIFICACIONES DE VIDEOS**

### **Formatos Soportados:**
- **MP4** (recomendado)
- **WebM**
- **MOV**
- **AVI**

### **Límites:**
- **Tamaño máximo**: 10MB
- **Duración máxima**: 15 segundos
- **Resolución recomendada**: 1080x1920 (vertical)
- **Aspecto**: 9:16 (vertical)

### **Características:**
- **Autoplay**: Reproducción automática
- **Muted**: Sin sonido por defecto
- **Loop**: Repetición continua
- **Responsive**: Adaptable a todos los dispositivos

## 🎨 **DISEÑO DE LA HISTORIA**

### **Sección Principal:**
- **Fondo**: Gradiente azul-púrpura
- **Layout**: 2 columnas (texto + video)
- **Video**: Controles de play/pause
- **Botones**: Catálogo y WhatsApp

### **Características Visuales:**
- **Gradiente de fondo**: `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`
- **Sombras**: `box-shadow: 0 20px 40px rgba(0,0,0,0.3)`
- **Bordes redondeados**: `border-radius: 20px`
- **Overlay**: Superposición sutil sobre el video

## 🔧 **FUNCIONALIDADES DEL PANEL ADMIN**

### **Gestión de Historias:**
1. **Crear Historia**: Formulario con subida de video
2. **Editar Historia**: Modificar título, descripción y video
3. **Activar/Desactivar**: Control de visibilidad
4. **Eliminar**: Borrado completo con archivos

### **Características del Formulario:**
- **Drag & Drop**: Arrastrar videos al área de subida
- **Preview**: Vista previa del video antes de guardar
- **Validación**: Tamaño, formato y duración
- **Información**: Detalles del archivo (tamaño, duración)

### **Estadísticas:**
- **Total de historias**: Activas e inactivas
- **Historia principal**: La más reciente y activa
- **Búsqueda**: Filtrar por título o descripción

## 📱 **EXPERIENCIA DE USUARIO**

### **En la Página Principal:**
1. **Video automático**: Se reproduce al cargar la página
2. **Controles intuitivos**: Botón de play/pause
3. **Responsive**: Se adapta a móvil y desktop
4. **Call-to-action**: Botones para catálogo y WhatsApp

### **En Móvil:**
- **Video a pantalla completa**: Experiencia inmersiva
- **Controles táctiles**: Fácil interacción
- **Carga optimizada**: Videos comprimidos

### **En Desktop:**
- **Layout de 2 columnas**: Texto y video lado a lado
- **Hover effects**: Interacciones suaves
- **Navegación con teclado**: Accesibilidad

## 🚀 **PROCESO DE DESPLIEGUE**

### **1. Preparar el Proyecto:**
```bash
# Compilar el proyecto
mvn clean package -DskipTests

# Verificar que se generó el JAR
ls -la target/oriola-denim-0.0.1-SNAPSHOT.jar
```

### **2. Configurar en Railway:**
1. **Crear proyecto** en Railway
2. **Conectar repositorio** GitHub
3. **Agregar servicio MySQL**
4. **Configurar variables** de entorno
5. **Desplegar** automáticamente

### **3. Configurar Variables:**
```bash
SPRING_PROFILES_ACTIVE=railway
ADMIN_USERNAME=admin
ADMIN_PASSWORD=tu_contraseña_segura
MAIL_USERNAME=luceroprograma@gmail.com
MAIL_PASSWORD=kmqh ktkl lhyj gwlf
```

### **4. Verificar Despliegue:**
1. **Aplicación funcionando**: `https://[proyecto].railway.app`
2. **Panel admin**: `https://[proyecto].railway.app/admin/historias`
3. **Historia visible**: En la página principal
4. **Videos funcionando**: Reproducción correcta

## 📊 **MÉTRICAS Y MONITOREO**

### **Logs de Railway:**
- **Build logs**: Proceso de compilación
- **Deploy logs**: Inicio de la aplicación
- **HTTP logs**: Requests HTTP
- **Error logs**: Errores de la aplicación

### **Métricas de Video:**
- **Tamaño de archivos**: Monitoreo de espacio
- **Tiempo de carga**: Optimización de rendimiento
- **Reproducciones**: Analytics de uso

## 🔒 **SEGURIDAD**

### **Validaciones Implementadas:**
- **Tipo de archivo**: Solo formatos de video permitidos
- **Tamaño máximo**: 10MB por video
- **Duración máxima**: 15 segundos
- **Sanitización**: Nombres de archivo seguros

### **Acceso al Panel:**
- **Autenticación**: Usuario y contraseña
- **Rutas protegidas**: Solo administradores
- **Sesiones seguras**: Invalidación automática

## 🎯 **PRÓXIMOS PASOS**

### **Después del Despliegue:**
1. **Crear primera historia** desde el panel admin
2. **Subir video promocional** de ORIOLA
3. **Configurar título y descripción** atractivos
4. **Activar la historia** para que aparezca en el index
5. **Probar en diferentes dispositivos**

### **Optimizaciones Futuras:**
- **CDN para videos**: Mejor rendimiento global
- **Compresión automática**: Reducir tamaño de archivos
- **Analytics avanzados**: Métricas de engagement
- **Múltiples historias**: Carrusel de historias

## 📞 **SOPORTE**

### **Si Hay Problemas:**
1. **Revisar logs** en Railway Dashboard
2. **Verificar variables** de entorno
3. **Comprobar base de datos** MySQL
4. **Probar subida** de videos

### **Contacto:**
- **WhatsApp**: 11-59293920
- **Email**: luceroprograma@gmail.com
- **Documentación**: Carpeta `documentacion/`

---

**¡El sistema de historias está listo para usar en Railway!** 🚀

**Fecha de implementación**: 14 de enero de 2025  
**Estado**: ✅ Completado y funcional  
**Próximo paso**: Desplegar en Railway y crear la primera historia
