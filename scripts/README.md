# 🚀 Scripts de Automatización - OriolaIndumentaria

Scripts para automatizar el despliegue y gestión de la aplicación en el servidor LightNode.

## 📁 Archivos Incluidos

- `deploy.sh` - Script de despliegue automático completo
- `menu-deploy.sh` - Menú interactivo para gestión
- `deploy.ps1` - Script PowerShell para Windows
- `README.md` - Este archivo con instrucciones

## 🖥️ Configuración Inicial (Primera vez)

### 1. Subir scripts al servidor Node

```bash
# Conectar al servidor
ssh root@149.104.92.116

# Crear directorio de scripts
mkdir -p /home/oriola/scripts

# Salir del servidor
exit
```

### 2. Copiar scripts desde Windows

```powershell
# Desde PowerShell en Windows
scp scripts/deploy.sh root@149.104.92.116:/home/oriola/scripts/
scp scripts/menu-deploy.sh root@149.104.92.116:/home/oriola/scripts/
```

### 3. Hacer scripts ejecutables

```bash
# Conectar al servidor
ssh root@149.104.92.116

# Hacer ejecutables
chmod +x /home/oriola/scripts/deploy.sh
chmod +x /home/oriola/scripts/menu-deploy.sh

# Crear enlaces simbólicos para fácil acceso
ln -s /home/oriola/scripts/deploy.sh /usr/local/bin/deploy
ln -s /home/oriola/scripts/menu-deploy.sh /usr/local/bin/menu
```

## 🚀 Uso de los Scripts

### Opción 1: Despliegue Automático Completo

```bash
# En el servidor Node
cd /home/oriola/OriolaIndumentaria
deploy
```

### Opción 2: Menú Interactivo

```bash
# En el servidor Node
cd /home/oriola/OriolaIndumentaria
menu
```

### Opción 3: Desde Windows (PowerShell)

```powershell
# Despliegue completo
.\scripts\deploy.ps1

# Menú interactivo
.\scripts\deploy.ps1 -Menu

# Acciones específicas
.\scripts\deploy.ps1 -Action stop
.\scripts\deploy.ps1 -Action start
.\scripts\deploy.ps1 -Action restart
.\scripts\deploy.ps1 -Action status
.\scripts\deploy.ps1 -Action logs
```

## 📋 Funcionalidades

### Script de Despliegue (deploy.sh)
- ✅ Para la aplicación actual
- ✅ Actualiza código desde GitHub
- ✅ Compila la aplicación
- ✅ Inicia en segundo plano
- ✅ Verifica que esté corriendo
- ✅ Muestra URLs de acceso

### Menú Interactivo (menu-deploy.sh)
- 🔄 Parar aplicación
- 📥 Actualizar código (git pull)
- 🔨 Compilar aplicación
- ▶️ Iniciar aplicación
- 🚀 Despliegue completo
- 📊 Ver estado del sistema
- 📝 Ver logs de la aplicación
- 🔄 Reiniciar aplicación
- ℹ️ Información del proyecto

### Script PowerShell (deploy.ps1)
- 🖥️ Ejecución desde Windows
- 🔗 Conexión SSH automática
- 📋 Menú interactivo
- ⚡ Acciones rápidas
- 📊 Monitoreo de estado

## 🛠️ Comandos Útiles

### Verificar estado de la aplicación
```bash
ps aux | grep java
```

### Ver logs en tiempo real
```bash
tail -f /home/oriola/OriolaIndumentaria/nohup.out
```

### Parar aplicación manualmente
```bash
pkill -f "oriola-denim-0.0.1-SNAPSHOT.jar"
```

### Verificar espacio en disco
```bash
df -h
```

### Ver uso de memoria
```bash
free -h
```

## 🔧 Solución de Problemas

### Error: "Permission denied"
```bash
chmod +x /home/oriola/scripts/deploy.sh
chmod +x /home/oriola/scripts/menu-deploy.sh
```

### Error: "Command not found"
```bash
# Verificar que los enlaces simbólicos existan
ls -la /usr/local/bin/deploy
ls -la /usr/local/bin/menu
```

### Error de compilación
```bash
# Limpiar y recompilar
mvn clean
mvn package -DskipTests
```

### Aplicación no inicia
```bash
# Ver logs de error
cat /home/oriola/OriolaIndumentaria/nohup.out

# Verificar puerto
netstat -tlnp | grep 8080
```

## 📞 URLs de Acceso

- **Aplicación:** http://149.104.92.116:8080
- **Dominio:** http://orioladenim.com.ar:8080
- **Admin:** http://149.104.92.116:8080/admin/login

## 🎯 Flujo de Trabajo Recomendado

1. **Desarrollo local** - Hacer cambios en localhost
2. **Commit y Push** - Subir cambios a GitHub
3. **Despliegue** - Ejecutar `deploy` en el servidor
4. **Verificación** - Probar la aplicación en producción
5. **Sincronización** - Restaurar backup en otros entornos

## 📝 Notas Importantes

- Los scripts asumen que estás en el directorio `/home/oriola/OriolaIndumentaria`
- El perfil activo es `lightnode` para producción
- Los logs se guardan en `nohup.out`
- La aplicación corre en el puerto 8080
- Se requiere conexión SSH configurada
