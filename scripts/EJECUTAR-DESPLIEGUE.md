# 🚀 Ejecutar Despliegue de Detodoya.com

## ✅ **ARCHIVOS PREPARADOS**

- ✅ `scripts/deploy-detodoya-donweb.sh` - Script de despliegue automático
- ✅ `scripts/menu-deploy-detodoya-donweb.sh` - Menú interactivo
- ✅ `scripts/COMANDOS-DESPLIEGUE-RAPIDO.txt` - Comandos rápidos
- ✅ `documentacion/servidor/PLAN-DESPLIEGUE-DETODOYA.md` - Plan completo

---

## 📝 **PASOS PARA EJECUTAR**

### **1. Conectar al servidor:**
```bash
ssh -p5638 root@149.50.144.53
# Contraseña: Qbasic.1977.server
```

### **2. Crear directorios (en el servidor):**
```bash
mkdir -p /home/detodoya/scripts
mkdir -p /home/detodoya/uploads
mkdir -p /home/detodoya/backups
chmod -R 755 /home/detodoya
```

### **3. Subir scripts (desde Windows PowerShell):**
```powershell
# Desde el directorio del proyecto en Windows
scp -P5638 scripts/deploy-detodoya-donweb.sh root@149.50.144.53:/home/detodoya/scripts/
scp -P5638 scripts/menu-deploy-detodoya-donweb.sh root@149.50.144.53:/home/detodoya/scripts/
```

### **4. Hacer scripts ejecutables (en el servidor):**
```bash
chmod +x /home/detodoya/scripts/*.sh
```

### **5. Clonar proyecto (en el servidor):**
```bash
cd /home/detodoya
git clone https://github.com/LuceroGustavo/detodoya-app.git Detodoya.com
# O el repositorio correcto
```

### **6. Verificar base de datos (en el servidor):**
```bash
mysql -u root -p
# Dentro de MySQL:
SHOW DATABASES;
# Debe aparecer 'detodoya'
exit;
```

### **7. Desplegar (en el servidor):**
```bash
cd /home/detodoya/Detodoya.com
/home/detodoya/scripts/deploy-detodoya-donweb.sh
```

---

## 🌐 **VERIFICAR**

- http://149.50.144.53:8080
- http://detodoya.com:8080

---

**¿Listo para empezar? Ejecuta el paso 1 para conectarte al servidor.**

