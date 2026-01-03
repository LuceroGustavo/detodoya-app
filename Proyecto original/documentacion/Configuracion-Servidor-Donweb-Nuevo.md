# Configuración del Servidor Donweb - ORIOLA Indumentaria (NUEVO)

**Fecha de creación:** 29 de diciembre de 2025  
**Servidor:** Donweb - Buenos Aires, Argentina  
**Propósito:** Servidor de producción para aplicación Spring Boot  
**Estado:** ✅ **APLICACIÓN DESPLEGADA Y FUNCIONANDO**  
**Última actualización:** 30 de diciembre de 2025

---

## 🖥️ **DATOS DEL SERVIDOR**

### **Información Básica:**
- **IP Pública IPv4:** `66.97.45.252` ✅
- **IP Pública IPv6:** `2800:6c0:5::845` ✅
- **MAC Address:** `fa:16:3e:1d:40:c0`
- **Hostname:** `vps-5549701-x.dattaweb.com` ✅
- **Puerto SSH:** `5625` ✅ (No es el puerto estándar 22)
- **Ubicación:** Buenos Aires, Argentina 🇦🇷
- **Nodo:** `yoga001`
- **Sistema Operativo:** ✅ **Ubuntu 24.04 UEFI Minimal** (Instalado)
- **Fecha de creación:** 29/12/2025, 10:38:11 am

### **Especificaciones:**
- **CPU:** 2 vCore ✅
- **RAM:** 2 GB ✅
- **Almacenamiento:** 20 GB contratado / 25 GB SSD total
- **Almacenamiento usado:** 2.87 GB de 25 GB (11.5% usado) ✅
- **Transferencia:** 0.02 GB de 1000 GB mensuales
- **Arquitectura:** UEFI
- **Tipo:** Cloud Server Donweb

---

## 🛠️ **SOFTWARE PREINSTALADO**

### **Versiones Detectadas:**
- **Git:** ✅ 2.43.0 (Ya instalado)
- **Linux Kernel:** 6.8.0
- **Perl:** 5.38.2
- **Postfix:** 3.8.6 (Servidor de correo)
- **Python:** 3.12.3

---

## 🔐 **ACCESO SSH**

### **Conexión SSH:**
```bash
# Comando completo de conexión
ssh -p5625 root@66.97.45.252

# O usando el hostname:
ssh -p5625 root@vps-5549701-x.dattaweb.com
```

**Credenciales:**
- **Usuario:** `root`
- **Contraseña:** `Qbasic.1977.oriola` ✅
- **Puerto SSH:** `5625` ✅ (⚠️ No es el puerto estándar 22)

**⚠️ IMPORTANTE:** 
- Anotar el puerto SSH exacto (puede no ser el estándar 22)
- Guardar la contraseña en lugar seguro

---

## 📊 **ESTADO ACTUAL DEL SERVIDOR**

### **Recursos:**
- **Disco:** 2.87 GB usado de 25 GB (11.5% usado) ✅
- **Memoria:** 2 GB RAM
- **CPU:** 2 vCore
- **Transferencia:** 0.02 GB de 1000 GB (prácticamente sin uso)

### **Estado:**
- ✅ **Servidor creado exitosamente**
- ✅ **Sistema Operativo instalado:** Ubuntu 24.04 UEFI Minimal
- ✅ **Servidor encendido desde:** 29/12/2025, 10:38:02 am
- ⏳ **Configuración pendiente:** Ver sección de checklist

---

## 📋 **CHECKLIST DE CONFIGURACIÓN**

### **FASE 1: VERIFICACIÓN INICIAL**
- [x] Servidor creado en Donweb ✅
- [x] Ubuntu 24.04 UEFI instalado ✅
- [x] IP Pública obtenida: `66.97.45.252` ✅
- [x] Puerto SSH verificado: `5625` ✅
- [x] Hostname obtenido: `vps-5549701-x.dattaweb.com` ✅
- [x] Conexión SSH probada y funcionando ✅
- [x] Sistema operativo verificado ✅

### **FASE 2: CONFIGURACIÓN INICIAL**
- [x] Actualizar sistema (`sudo apt update && sudo apt upgrade`) ✅
- [x] Configurar clave SSH (opcional pero recomendado) ✅
- [x] Configurar firewall UFW ✅
- [x] **Configurar firewall Donweb (CRÍTICO)** ✅
  - Puerto SSH (5625): IPv4 e IPv6 configurados ✅
  - Puerto HTTP (80): IPv4 e IPv6 configurados ✅
  - Puerto HTTPS (443): IPv4 e IPv6 configurados ✅
  - Puerto Aplicación (8080): IPv4 e IPv6 configurados ✅

### **FASE 3: INSTALACIÓN DE SOFTWARE**
- [x] Instalar Java 17 (OpenJDK) ✅ (Versión: 17.0.17)
- [x] Instalar MySQL 8.0 ✅ (Versión: 8.0.44)
- [x] Instalar Maven ✅ (Versión: 3.8.7)
- [x] Instalar Nginx ✅ (Versión: 1.24.0)

### **FASE 4: CONFIGURACIÓN DE BASE DE DATOS**
- [x] Configurar MySQL (usuario root) ✅
- [x] Crear base de datos `orioladenim` ✅
- [x] Crear usuario `oriola_user` ✅
- [x] Verificar permisos y conexión ✅
- [x] Usuario configurado con contraseña: `OriolaDB2025!` ✅

### **FASE 5: CONFIGURACIÓN DE APLICACIÓN**
- [x] Crear directorios (`/home/oriola/uploads`, etc.) ✅
- [x] Clonar repositorio desde GitHub ✅
- [x] Repositorio configurado como privado ✅
- [x] Autenticación con Personal Access Token configurada ✅
- [x] Crear/verificar `application-donweb.properties` ✅
- [x] Configurar `server.address=0.0.0.0` ✅
- [x] Compilar aplicación ✅ (JAR: 72MB)
- [x] Desplegar aplicación ✅
- [x] Scripts de despliegue configurados y funcionando ✅
  - `/home/oriola/deploy` - Script automático de despliegue ✅
  - `/home/oriola/menu` - Menú interactivo de gestión ✅

### **FASE 6: VERIFICACIÓN FINAL**
- [x] Verificar que aplicación está corriendo ✅
- [x] Probar acceso desde Internet ✅ (http://66.97.45.252:8080)
- [x] Verificar logs ✅
- [x] Probar funcionalidades básicas ✅
- [x] Probar flujo completo de despliegue (commit → push → pull → restart) ✅

---

## 🔥 **CONFIGURACIÓN DE FIREWALL**

### **Firewall UFW (Ubuntu):**
```bash
# Configurar después de conectarse vía SSH
sudo ufw allow [PUERTO_SSH]/tcp  # ⚠️ Usar el puerto SSH correcto
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable
```

### **⚠️ CRÍTICO: Firewall Donweb (Panel)**

**URL del panel de firewall:**
- Acceder a: https://micuenta.donweb.com/es-ar/servicios/cloud-iaas/vps/[ID_SERVIDOR]/configurar/firewall
- Reemplazar `[ID_SERVIDOR]` con el ID de tu servidor

**Reglas a configurar:**
1. **SSH:**
   - Protocolo: TCP
   - Puerto: `[PUERTO_SSH]` (el que uses para SSH)
   - IPv4: `0.0.0.0/0`
   - IPv6: `::/0`

2. **Aplicación:**
   - Protocolo: TCP
   - Puerto: `8080`
   - IPv4: `0.0.0.0/0`
   - IPv6: `::/0`

**⚠️ SIN ESTA CONFIGURACIÓN, LA APLICACIÓN NO SERÁ ACCESIBLE DESDE INTERNET**

---

## ⚙️ **CONFIGURACIÓN DE LA APLICACIÓN**

### **Archivo de configuración:**
- **`application-donweb.properties`** - Configuración específica para este servidor

### **Configuración de base de datos:**
- **URL:** `jdbc:mysql://localhost:3306/orioladenim`
- **Usuario:** `oriola_user`
- **Contraseña:** `OriolaDB2025!`

### **Configuración de archivos:**
- **Uploads:** `/home/oriola/uploads`
- **Backups:** `/home/oriola/backups`

### **Perfil activo:**
- **Comando:** `--spring.profiles.active=donweb`

### **Configuración crítica de red:**
- **`server.address=0.0.0.0`** en `application-donweb.properties` ✅
  - Permite que Spring Boot escuche en todas las interfaces de red
  - Sin esto, la aplicación solo escucha en localhost y no es accesible desde fuera

---

## 📊 **COMPARACIÓN CON SERVIDOR ANTERIOR**

| Aspecto | Servidor Anterior (149.50.144.53) | Servidor Nuevo (66.97.45.252) |
|--------|----------------------------------|-------------------------------|
| **IP Pública** | 149.50.144.53 | 66.97.45.252 ✅ |
| **Ubicación** | Buenos Aires, AR | Buenos Aires, AR |
| **Sistema Operativo** | Ubuntu 24.04 UEFI | Ubuntu 24.04 UEFI ✅ |
| **CPU** | 1 vCore | 2 vCore ✅ (mejor) |
| **RAM** | 2 GB | 2 GB |
| **Almacenamiento** | 50 GB SSD | 25 GB SSD |
| **Git** | 2.43.0 | 2.43.0 ✅ |
| **Kernel** | 6.8.0 | 6.8.0 ✅ |

---

## 🚀 **PRÓXIMOS PASOS INMEDIATOS**

1. **Verificar puerto SSH:**
   - Revisar en el panel de Donweb qué puerto SSH está configurado
   - Anotarlo en este documento

2. **Conectarse vía SSH:**
   ```bash
   ssh -p[PUERTO_SSH] root@66.97.45.252
   ```

3. **Seguir la guía completa:**
   - Ver: `Guia-Configuracion-Nuevo-Servidor-Donweb.md`
   - Seguir paso a paso desde la sección 2

---

## 📞 **CONTACTO Y SOPORTE**

### **Donweb:**
- **Sitio web:** https://micuenta.donweb.com/
- **Soporte:** Disponible en español
- **Panel de control:** Acceso desde https://micuenta.donweb.com/

### **Documentación del proyecto:**
- **Repositorio:** https://github.com/LuceroGustavo/OriolaIndumentaria (🔒 **PRIVADO**)
- **Rama principal:** `master`
- **Guía completa:** `Guia-Configuracion-Nuevo-Servidor-Donweb.md`
- **Configuración repositorio privado:** Ver `Configurar-Repositorio-Privado.md`

---

## ✅ **ESTADO ACTUAL**

**Última actualización:** 30 de diciembre de 2025  
**Estado:** ✅ **APLICACIÓN DESPLEGADA Y FUNCIONANDO**  
**IP Pública:** `66.97.45.252`  
**URL de acceso:** http://66.97.45.252:8080

### **Completado:**
- [x] Servidor Cloud creado exitosamente en Donweb ✅
- [x] Sistema Operativo Ubuntu 24.04 UEFI instalado ✅
- [x] IP Pública obtenida: `66.97.45.252` ✅
- [x] Recursos verificados: 2 vCPU, 2 GB RAM, 25 GB SSD ✅
- [x] Conexión SSH configurada con clave ✅
- [x] Sistema actualizado ✅
- [x] Java 17 instalado (OpenJDK 17.0.17) ✅
- [x] MySQL 8.0 instalado (Versión 8.0.44) ✅
- [x] Maven 3.8.7 instalado ✅
- [x] Nginx 1.24.0 instalado ✅
- [x] Firewall UFW configurado ✅
- [x] Firewall Donweb configurado (puertos 80, 443, 5625, 8080) ✅
- [x] Base de datos MySQL configurada (BD: `orioladenim`, Usuario: `oriola_user`) ✅
- [x] Directorios de aplicación creados ✅
- [x] Repositorio clonado desde GitHub ✅
- [x] Repositorio configurado como privado ✅
- [x] Autenticación con Personal Access Token configurada (servidor y local) ✅
- [x] Aplicación compilada ✅ (JAR: 72MB)
- [x] Aplicación desplegada y funcionando ✅
- [x] Scripts de despliegue configurados (`/home/oriola/deploy` y `/home/oriola/menu`) ✅
- [x] Acceso desde Internet verificado ✅
- [x] Flujo completo de despliegue probado (commit → push → pull → restart) ✅

### **Pendiente:**
- [ ] Configurar dominio personalizado (DNS)
- [ ] Configurar SSL/HTTPS con Let's Encrypt
- [ ] Configurar Nginx como reverse proxy (opcional)
- [ ] Migrar datos del servidor anterior (si aplica)
- [ ] Optimizaciones adicionales

---

---

## 🚀 **DESPLIEGUE Y GESTIÓN**

### **Scripts de Despliegue:**

Los siguientes scripts están disponibles en el servidor para gestionar la aplicación:

#### **1. Script de Menú Interactivo:**
```bash
/home/oriola/menu
# O simplemente:
menu
```

**Opciones disponibles:**
- Ver estado de la aplicación
- Iniciar aplicación
- Detener aplicación
- Reiniciar aplicación
- **Opción 5: Detener, Pull, Compilar y Reiniciar** (flujo completo de despliegue)
- Ver logs
- Salir

#### **2. Script Automático de Despliegue:**
```bash
/home/oriola/deploy
# O simplemente:
deploy
```

Este script ejecuta automáticamente:
1. Detener la aplicación
2. Hacer `git pull` desde el repositorio
3. Compilar con Maven
4. Iniciar la aplicación

### **Flujo de Trabajo Recomendado:**

1. **Desarrollo local:**
   ```bash
   # Hacer cambios en el código
   git add .
   git commit -m "Descripción del cambio"
   git push origin master
   ```

2. **Despliegue en servidor:**
   ```bash
   # Conectarse al servidor
   ssh -p5625 root@66.97.45.252
   
   # Ejecutar script de despliegue
   /home/oriola/menu
   # Seleccionar opción 5
   ```

---

## 📝 **CONFIGURACIÓN DE REPOSITORIO PRIVADO**

### **Estado:**
- ✅ Repositorio cambiado a privado en GitHub
- ✅ Personal Access Token creado: `Oriola-Server-Deploy`
- ✅ Autenticación configurada en servidor (URL con token)
- ✅ Autenticación configurada localmente (URL con token)

### **Detalles:**
- **Token:** Configurado en URL del repositorio remoto
- **Servidor:** `/home/oriola/OriolaIndumentaria/.git/config`
- **Local:** `.git/config`
- **Documentación completa:** Ver `Configurar-Repositorio-Privado.md`

---

## 🔐 **INFORMACIÓN SENSIBLE**

**⚠️ IMPORTANTE:** Este documento contiene información sensible del servidor:
- Contraseñas de acceso
- Tokens de autenticación
- Credenciales de base de datos

**Mantener este documento seguro y no compartirlo públicamente.**

---

## 📋 **PRÓXIMOS PASOS**

### **Inmediatos:**
1. ✅ Aplicación desplegada y funcionando
2. ⏳ Configurar dominio personalizado
3. ⏳ Configurar SSL/HTTPS

### **Futuros:**
- Migración de datos del servidor anterior (si aplica)
- Configuración de Nginx como reverse proxy
- Optimizaciones de rendimiento
- Configuración de backups automáticos

---

**Nota:** Este documento contiene información sensible del servidor. Mantenerlo seguro y actualizado conforme se avance en la configuración.

**Estado actual:** ✅ **APLICACIÓN DESPLEGADA Y FUNCIONANDO CORRECTAMENTE**

