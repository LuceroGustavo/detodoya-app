# Plan de Despliegue - Detodoya.com en Servidor Donweb

**Fecha:** 28 de diciembre de 2025  
**Servidor:** Donweb - Buenos Aires, Argentina  
**IP:** 149.50.144.53  
**Puerto SSH:** 5638  
**Estado:** ⏳ Pendiente de despliegue

---

## 📋 **SITUACIÓN ACTUAL**

### **Servidor Donweb:**
- ✅ Servidor configurado y funcionando
- ✅ Java 17, MySQL 8.0, Maven instalados
- ✅ Firewall configurado (puerto 8080 abierto)
- ✅ Dominio detodoya.com configurado

### **Aplicaciones en el Servidor:**
- **Fulbito:** Puerto 8081 (NO TOCAR - aplicación virtual que no se despliega como JAR)
- **Detodoya:** Puerto 8080 (a desplegar)

### **Proyecto Anterior (Orioladenim):**
- Scripts disponibles: `deploy-donweb.sh` y `menu-deploy-donweb.sh`
- Directorio: `/home/oriola/OriolaIndumentaria`
- JAR: `oriola-denim-0.0.1-SNAPSHOT.jar`
- Base de datos: `orioladenim`
- Usuario DB: `oriola_user`

---

## 🎯 **OBJETIVO**

Adaptar los scripts de despliegue de Orioladenim para Detodoya.com, asegurando que:
1. Detodoya use el puerto 8080 (disponible)
2. Fulbito siga funcionando en 8081 (no tocar)
3. Los scripts funcionen correctamente con el nuevo proyecto

---

## 📝 **CAMBIOS NECESARIOS**

### **1. Información del Proyecto:**
- **Nombre del proyecto:** `Detodoya` (en lugar de `OriolaIndumentaria`)
- **JAR generado:** `detodoya-0.0.1-SNAPSHOT.jar` (en lugar de `oriola-denim-0.0.1-SNAPSHOT.jar`)
- **Directorio en servidor:** `/home/detodoya/Detodoya.com` (nuevo)
- **Base de datos:** `detodoya` (ya configurada)
- **Usuario DB:** `detodoya_user` (ya configurado)
- **Perfil Spring:** `donweb` (igual)

### **2. Configuración del Servidor:**
- **Puerto:** 8080 ✅ (disponible, fulbito usa 8081)
- **Directorio de uploads:** `/home/detodoya/uploads`
- **Directorio de backups:** `/home/detodoya/backups`

### **3. Scripts a Adaptar:**
- `scripts/deploy-donweb.sh` → Adaptar para Detodoya
- `scripts/menu-deploy-donweb.sh` → Adaptar para Detodoya

---

## 🚀 **PLAN DE ACCIÓN**

### **FASE 1: Preparación del Servidor** ⏳

1. **Conectar al servidor:**
   ```bash
   ssh -p5638 root@149.50.144.53
   ```

2. **Crear directorio para Detodoya:**
   ```bash
   mkdir -p /home/detodoya
   cd /home/detodoya
   ```

3. **Clonar repositorio de Detodoya:**
   ```bash
   git clone https://github.com/LuceroGustavo/detodoya-app.git Detodoya.com
   # O el repositorio correcto donde esté Detodoya
   ```

4. **Verificar que la base de datos existe:**
   ```bash
   mysql -u root -p
   # Dentro de MySQL:
   SHOW DATABASES;  # Debe aparecer 'detodoya'
   USE detodoya;
   SHOW TABLES;  # Verificar tablas
   ```

5. **Crear directorios necesarios:**
   ```bash
   mkdir -p /home/detodoya/uploads
   mkdir -p /home/detodoya/backups
   chmod -R 755 /home/detodoya
   ```

### **FASE 2: Adaptación de Scripts** ⏳

1. **Adaptar `deploy-donweb.sh`:**
   - Cambiar referencias de `oriola-denim-0.0.1-SNAPSHOT.jar` → `detodoya-0.0.1-SNAPSHOT.jar`
   - Cambiar directorio de `/home/oriola/OriolaIndumentaria` → `/home/detodoya/Detodoya.com`
   - Cambiar mensajes de "ORIOLA INDUMENTARIA" → "DETODOYA.COM"
   - Mantener perfil `donweb`

2. **Adaptar `menu-deploy-donweb.sh`:**
   - Mismos cambios que en `deploy-donweb.sh`
   - Actualizar rutas y nombres

3. **Subir scripts al servidor:**
   ```bash
   # Desde Windows (PowerShell)
   scp -P5638 scripts/deploy-donweb.sh root@149.50.144.53:/home/detodoya/scripts/
   scp -P5638 scripts/menu-deploy-donweb.sh root@149.50.144.53:/home/detodoya/scripts/
   ```

4. **Hacer scripts ejecutables:**
   ```bash
   # En el servidor
   chmod +x /home/detodoya/scripts/deploy-donweb.sh
   chmod +x /home/detodoya/scripts/menu-deploy-donweb.sh
   ```

### **FASE 3: Primer Despliegue** ⏳

1. **Compilar y desplegar:**
   ```bash
   cd /home/detodoya/Detodoya.com
   ./scripts/deploy-donweb.sh
   # O usar el menú:
   ./scripts/menu-deploy-donweb.sh
   ```

2. **Verificar que la aplicación está corriendo:**
   ```bash
   ps aux | grep detodoya
   netstat -tlnp | grep 8080
   ```

3. **Probar acceso:**
   - http://149.50.144.53:8080
   - http://detodoya.com:8080 (si el DNS está configurado)

### **FASE 4: Verificación** ⏳

1. **Verificar que Fulbito sigue funcionando:**
   ```bash
   ps aux | grep 8081
   # Debe estar corriendo en 8081
   ```

2. **Verificar logs de Detodoya:**
   ```bash
   tail -f /home/detodoya/Detodoya.com/app.log
   ```

3. **Probar funcionalidades básicas:**
   - Acceso al admin
   - Crear producto
   - Subir imágenes
   - Ver catálogo público

---

## ⚠️ **PUNTOS CRÍTICOS**

1. **NO TOCAR Fulbito en puerto 8081** - Es una aplicación virtual que no se despliega como JAR
2. **Verificar puerto 8080 disponible** - Debe estar libre antes de desplegar
3. **Base de datos `detodoya` debe existir** - Verificar antes de desplegar
4. **Usuario `detodoya_user` debe tener permisos** - Verificar en MySQL
5. **Firewall Donweb debe tener puerto 8080 abierto** - Ya configurado según documentación

---

## 📊 **COMPARACIÓN: Orioladenim vs Detodoya**

| Aspecto | Orioladenim | Detodoya |
|---------|-------------|----------|
| **JAR** | `oriola-denim-0.0.1-SNAPSHOT.jar` | `detodoya-0.0.1-SNAPSHOT.jar` |
| **Directorio** | `/home/oriola/OriolaIndumentaria` | `/home/detodoya/Detodoya.com` |
| **Base de datos** | `orioladenim` | `detodoya` |
| **Usuario DB** | `oriola_user` | `detodoya_user` |
| **Puerto** | 8080 | 8080 |
| **Perfil Spring** | `donweb` | `donweb` |
| **Uploads** | `/home/oriola/uploads` | `/home/detodoya/uploads` |

---

## 🔧 **COMANDOS RÁPIDOS**

### **Conectar al servidor:**
```bash
ssh -p5638 root@149.50.144.53
```

### **Ir al proyecto:**
```bash
cd /home/detodoya/Detodoya.com
```

### **Despliegue completo:**
```bash
cd /home/detodoya/Detodoya.com
./scripts/deploy-donweb.sh
```

### **Menú interactivo:**
```bash
cd /home/detodoya/Detodoya.com
./scripts/menu-deploy-donweb.sh
```

### **Ver estado:**
```bash
ps aux | grep detodoya
netstat -tlnp | grep 8080
```

### **Ver logs:**
```bash
tail -f /home/detodoya/Detodoya.com/app.log
```

---

## ✅ **CHECKLIST PRE-DESPLIEGUE**

- [ ] Servidor accesible vía SSH
- [ ] Base de datos `detodoya` creada
- [ ] Usuario `detodoya_user` configurado con permisos
- [ ] Directorio `/home/detodoya` creado
- [ ] Repositorio clonado en `/home/detodoya/Detodoya.com`
- [ ] Scripts adaptados y subidos al servidor
- [ ] Scripts con permisos de ejecución
- [ ] Puerto 8080 disponible (verificar que no hay otra app)
- [ ] Puerto 8081 ocupado por Fulbito (verificar que sigue funcionando)
- [ ] Firewall Donweb tiene puerto 8080 abierto
- [ ] `application-donweb.properties` configurado correctamente

---

## 📝 **NOTAS IMPORTANTES**

1. **Fulbito NO debe tocarse** - Está en puerto 8081 y es una aplicación virtual
2. **El dominio detodoya.com debe estar configurado** - Verificar DNS
3. **Los scripts deben adaptarse ANTES de subirlos** - No usar los scripts de orioladenim directamente
4. **Hacer backup antes del primer despliegue** - Por si acaso
5. **Verificar que no haya conflictos de puertos** - 8080 para Detodoya, 8081 para Fulbito

---

**Última actualización:** 28 de diciembre de 2025  
**Estado:** ⏳ Plan creado, pendiente de ejecución

