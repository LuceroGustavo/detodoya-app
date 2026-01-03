# Instrucciones para Configurar Email SMTP con Gmail - ORIOLA Denim

**Fecha de creación:** 31 de diciembre de 2025  
**Propósito:** Guía para el cliente sobre cómo configurar Gmail para el envío automático de emails desde la aplicación

---

## 📧 **RESPUESTA AUTOMÁTICA ACTUAL**

Cuando un cliente envía un formulario de contacto, el sistema envía automáticamente un correo de confirmación con el siguiente contenido:

```
¡Hola [Nombre del Cliente]!

Gracias por contactarte con ORIOLA Denim. Hemos recibido tu consulta y te responderemos pronto.

📋 RESUMEN DE TU CONSULTA:
• Fecha: [fecha y hora]
• Asunto: [asunto de la consulta]
• Producto: [producto de interés]

💬 TU MENSAJE:
[mensaje que escribió el cliente]

📞 CONTACTO:
• WhatsApp: 54 11 6857 0940
• Email: orioladenim@gmail.com
• Web: orioladenim.com.ar

¡Gracias por elegir ORIOLA Denim!

---
ORIOLA Denim - Indumentaria con Estilo y Personalidad
```

**Asunto del correo:** `Confirmación de consulta - ORIOLA Denim`

---

## ⚙️ **QUÉ NECESITA EL CLIENTE**

Para que la aplicación pueda enviar correos automáticamente desde `orioladenim@gmail.com`, el cliente necesita generar una **"App Password" (Contraseña de Aplicación)** de Gmail.

**⚠️ IMPORTANTE:** 
- **NO** es necesario abrir puertos en el servidor
- **NO** es necesario configurar nada en el firewall
- Solo se necesita la **App Password** de Gmail
- Gmail ya tiene los puertos 587 (SMTP) y 465 (SMTPS) abiertos por defecto

---

## 📋 **PASOS PARA EL CLIENTE**

### **Paso 1: Activar Verificación en Dos Pasos**

1. Ir a la cuenta de Google: https://myaccount.google.com/
2. Hacer clic en **"Seguridad"** (menú izquierdo)
3. Buscar la sección **"Verificación en dos pasos"**
4. Si no está activada, hacer clic en **"Activar"** y seguir las instrucciones
5. Si ya está activada, continuar al Paso 2

**⚠️ NOTA:** La verificación en dos pasos **DEBE estar activada** para poder generar una App Password.

---

### **Paso 2: Generar App Password**

1. Ir directamente a: https://myaccount.google.com/apppasswords
   - O desde "Seguridad" → "Contraseñas de aplicaciones"

2. Si es la primera vez, Google puede pedir que confirmes tu identidad (contraseña)

3. En la página de "Contraseñas de aplicaciones":
   - **Seleccionar "Aplicación":** `Correo`
   - **Seleccionar "Dispositivo":** `Otro (nombre personalizado)`
   - **Escribir un nombre:** `Oriola App Server` (o cualquier nombre descriptivo)
   - Hacer clic en **"Generar"**

4. **Google mostrará una contraseña de 16 caracteres:**
   - Formato: `xxxx xxxx xxxx xxxx` (con espacios)
   - **⚠️ IMPORTANTE:** Copiar esta contraseña inmediatamente, porque solo se muestra una vez
   - Ejemplo: `abcd efgh ijkl mnop`

5. **Proporcionar la App Password al desarrollador:**
   - Enviar la contraseña de forma segura (por WhatsApp, email cifrado, etc.)
   - **NO compartirla públicamente**
   - La contraseña debe enviarse **sin espacios** (ej: `abcdefghijklmnop`)

---

## 🔧 **CONFIGURACIÓN ACTUAL EN EL SERVIDOR**

**Archivo:** `src/main/resources/application-donweb.properties`

**Configuración actual (temporal):**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:luceroprograma@gmail.com}
spring.mail.password=${MAIL_PASSWORD:kmqh ktkl lhyj gwlf}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

**Configuración que se necesita (después de recibir la App Password):**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:orioladenim@gmail.com}
spring.mail.password=${MAIL_PASSWORD:[APP_PASSWORD_DEL_CLIENTE]}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

**También en `EmailService.java`:**
```java
@Value("${app.email.from:orioladenim@gmail.com}")
private String fromEmail;

@Value("${app.email.to:orioladenim@gmail.com}")
private String toEmail;
```

---

## ✅ **VERIFICACIÓN**

Una vez configurada la App Password, el sistema enviará:

1. **Correo de confirmación al cliente** (automático cuando envía formulario)
2. **Correo de notificación al administrador** (a `orioladenim@gmail.com`)

---

## 🔒 **SEGURIDAD**

- La App Password es más segura que usar la contraseña principal de Gmail
- Si se compromete la App Password, se puede revocar sin afectar la cuenta principal
- La App Password solo permite enviar correos, no acceder a la cuenta completa
- Se recomienda generar una App Password específica para la aplicación

---

## 📞 **SOPORTE**

Si el cliente tiene problemas:
- Verificar que la verificación en dos pasos esté activada
- Asegurarse de copiar la App Password completa (16 caracteres)
- Enviar la App Password sin espacios al desarrollador
- Si se pierde la App Password, generar una nueva

---

**Última actualización:** 31 de diciembre de 2025

