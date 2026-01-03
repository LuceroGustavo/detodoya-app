# Comandos Rápidos - LightNode

**Servidor:** 149.104.92.116  
**Usuario:** root  
**Contraseña:** Qbasic.1977.server!

## 🚀 **COMANDOS BÁSICOS**

### **Conectar al servidor:**
```bash
ssh root@149.104.92.116
```

### **Navegar al proyecto:**
```bash
cd /home/oriola/OriolaIndumentaria
```

### **Ver estado de la aplicación:**
```bash
ps aux | grep java
```

### **Parar la aplicación:**
```bash
pkill -f "oriola-denim"
```

## 🔄 **ACTUALIZAR APLICACIÓN**

### **Método Rápido (Recomendado):**
```bash
# 1. Conectar
ssh root@149.104.92.116

# 2. Entrar al proyecto
cd /home/oriola/OriolaIndumentaria

# 3. Parar aplicación
pkill -f "oriola-denim"

# 4. Actualizar código
git pull origin master

# 5. Recompilar
mvn clean package -DskipTests

# 6. Ejecutar en segundo plano
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=lightnode > /dev/null 2>&1 &

# 7. Verificar
ps aux | grep java

# 8. Salir
exit
```

### **Método Completo (Para cambios grandes):**
```bash
# 1. Conectar
ssh root@149.104.92.116

# 2. Eliminar proyecto anterior
rm -rf /home/oriola/OriolaIndumentaria

# 3. Clonar desde cero
cd /home/oriola
git clone https://github.com/LuceroGustavo/OriolaIndumentaria.git

# 4. Entrar al proyecto
cd OriolaIndumentaria

# 5. Compilar
mvn clean package -DskipTests

# 6. Ejecutar en segundo plano
nohup java -jar target/oriola-denim-0.0.1-SNAPSHOT.jar --spring.profiles.active=lightnode > /dev/null 2>&1 &

# 7. Verificar
ps aux | grep java

# 8. Salir
exit
```

## 🔍 **COMANDOS DE DIAGNÓSTICO**

### **Ver logs del sistema:**
```bash
journalctl -f
```

### **Ver puertos en uso:**
```bash
netstat -tlnp | grep 8080
```

### **Ver uso de memoria:**
```bash
free -h
```

### **Ver uso de disco:**
```bash
df -h
```

## 🌐 **URLs DE ACCESO**

- **Aplicación:** http://149.104.92.116:8080
- **Admin:** http://149.104.92.116:8080/admin
- **Usuario admin:** admin
- **Contraseña admin:** OriolaAdmin2025!

## 📁 **ESTRUCTURA DE ARCHIVOS**

```
/home/oriola/
├── OriolaIndumentaria/          # Código fuente
│   └── target/
│       └── oriola-denim-0.0.1-SNAPSHOT.jar
├── uploads/                     # Imágenes (PERSISTENTE)
└── backups/                     # Backups (PERSISTENTE)
```

## ⚠️ **NOTAS IMPORTANTES**

- **Los archivos en `/home/oriola/uploads` y `/home/oriola/backups` persisten** entre reinicios
- **La aplicación se ejecuta con `nohup`** para que no se cierre al desconectar SSH
- **Siempre usar el perfil `lightnode`** en producción
- **La base de datos MySQL persiste** automáticamente

---

**Última actualización:** 11 de Octubre, 2025  
**Commit:** 60
