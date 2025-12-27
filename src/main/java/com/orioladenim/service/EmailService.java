package com.orioladenim.service;

import com.orioladenim.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from:luceroprograma@gmail.com}")
    private String fromEmail;

    @Value("${app.email.to:luceroprograma@gmail.com}")
    private String toEmail;

    /**
     * Envía notificación de nueva consulta por correo
     */
    public void sendNewContactNotification(Contact contact) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Nueva consulta de cliente - ORIOLA Denim");
            
            String emailBody = buildEmailBody(contact);
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("✅ Correo enviado exitosamente para consulta ID: " + contact.getId());
        } catch (Exception e) {
            System.err.println("❌ Error enviando correo para consulta ID: " + contact.getId() + " - " + e.getMessage());
        }
    }

    /**
     * Envía confirmación al cliente
     */
    public void sendConfirmationToClient(Contact contact) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(contact.getEmail());
            message.setSubject("Confirmación de consulta - ORIOLA Denim");
            
            String emailBody = buildClientConfirmationBody(contact);
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("✅ Correo de confirmación enviado a: " + contact.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Error enviando confirmación a: " + contact.getEmail() + " - " + e.getMessage());
        }
    }

    /**
     * Construye el cuerpo del correo para notificación interna
     */
    private String buildEmailBody(Contact contact) {
        StringBuilder body = new StringBuilder();
        body.append("🔔 NUEVA CONSULTA DE CLIENTE\n");
        body.append("================================\n\n");
        
        body.append("📋 INFORMACIÓN DEL CLIENTE:\n");
        body.append("• Nombre: ").append(contact.getNombre()).append("\n");
        body.append("• Email: ").append(contact.getEmail()).append("\n");
        if (contact.getTelefono() != null && !contact.getTelefono().trim().isEmpty()) {
            body.append("• Teléfono: ").append(contact.getTelefono()).append("\n");
        }
        body.append("• Fecha: ").append(contact.getFechaCreacion()).append("\n");
        body.append("• Ubicación: ").append(contact.getIpAddress()).append("\n\n");
        
        if (contact.getAsunto() != null && !contact.getAsunto().trim().isEmpty()) {
            body.append("📌 ASUNTO:\n");
            body.append(contact.getAsunto()).append("\n\n");
        }
        
        if (contact.getProductoInteres() != null && !contact.getProductoInteres().trim().isEmpty()) {
            body.append("🛍️ PRODUCTO DE INTERÉS:\n");
            body.append(contact.getProductoInteres()).append("\n\n");
        }
        
        body.append("💬 MENSAJE:\n");
        body.append(contact.getMensaje()).append("\n\n");
        
        body.append("🔗 ACCIONES:\n");
        body.append("• Ver en admin: http://localhost:8080/admin/contacts/").append(contact.getId()).append("\n");
        body.append("• Responder desde el panel de administración\n\n");
        
        body.append("---\n");
        body.append("Este correo fue enviado automáticamente por el sistema ORIOLA Denim");
        
        return body.toString();
    }

    /**
     * Construye el cuerpo del correo de confirmación para el cliente
     */
    private String buildClientConfirmationBody(Contact contact) {
        StringBuilder body = new StringBuilder();
        body.append("¡Hola ").append(contact.getNombre()).append("!\n\n");
        body.append("Gracias por contactarte con ORIOLA Denim. Hemos recibido tu consulta y te responderemos pronto.\n\n");
        
        body.append("📋 RESUMEN DE TU CONSULTA:\n");
        body.append("• Fecha: ").append(contact.getFechaCreacion()).append("\n");
        if (contact.getAsunto() != null && !contact.getAsunto().trim().isEmpty()) {
            body.append("• Asunto: ").append(contact.getAsunto()).append("\n");
        }
        if (contact.getProductoInteres() != null && !contact.getProductoInteres().trim().isEmpty()) {
            body.append("• Producto: ").append(contact.getProductoInteres()).append("\n");
        }
        
        body.append("\n💬 TU MENSAJE:\n");
        body.append(contact.getMensaje()).append("\n\n");
        
        body.append("📞 CONTACTO:\n");
        body.append("• WhatsApp: +54 9 11 1234-5678\n");
        body.append("• Email: info@orioladenim.com.ar\n");
        body.append("• Web: orioladenim.com.ar\n\n");
        
        body.append("¡Gracias por elegir ORIOLA Denim!\n\n");
        body.append("---\n");
        body.append("ORIOLA Denim - Indumentaria con Estilo y Personalidad");
        
        return body.toString();
    }
    
    // Enviar respuesta al cliente
    public void sendResponseToClient(Contact contact) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(contact.getEmail());
        String asuntoOriginal = contact.getAsunto() != null && !contact.getAsunto().trim().isEmpty() 
            ? contact.getAsunto() 
            : "Consulta";
        message.setSubject("Re: " + asuntoOriginal + " - ORIOLA Denim");

        String emailContent = buildClientResponseContent(contact);
        message.setText(emailContent);

        try {
            mailSender.send(message);
            System.out.println("✅ Respuesta enviada al cliente: " + contact.getEmail());
        } catch (MailException e) {
            System.err.println("❌ Error enviando respuesta a: " + contact.getEmail() + " - " + e.getMessage());
        }
    }
    
    private String buildClientResponseContent(Contact contact) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaRespuesta = contact.getFechaRespuesta() != null ? contact.getFechaRespuesta().format(formatter) : "N/A";
        String productoInteres = contact.getProductoInteres() != null && !contact.getProductoInteres().isEmpty() ? contact.getProductoInteres() : "General";

        return "¡Hola " + contact.getNombre() + "!\n\n" +
               "Hemos recibido tu consulta y te respondemos a continuación:\n\n" +
               "📋 DETALLES DE TU CONSULTA:\n" +
               "• Fecha: " + (contact.getFechaCreacion() != null ? contact.getFechaCreacion().format(formatter) : "N/A") + "\n" +
               "• Asunto: " + (contact.getAsunto() != null ? contact.getAsunto() : "Consulta") + "\n" +
               "• Producto: " + productoInteres + "\n\n" +
               "💬 TU MENSAJE:\n" +
               contact.getMensaje() + "\n\n" +
               "📝 NUESTRA RESPUESTA:\n" +
               contact.getRespuesta() + "\n\n" +
               "📅 Fecha de respuesta: " + fechaRespuesta + "\n\n" +
               "📞 CONTACTO ADICIONAL:\n" +
               "• WhatsApp: +54 9 11 1234-5678\n" +
               "• Email: info@orioladenim.com.ar\n" +
               "• Web: orioladenim.com.ar\n\n" +
               "¡Gracias por elegir ORIOLA Denim!\n\n" +
               "---\n" +
               "ORIOLA Denim - Indumentaria con Estilo y Personalidad";
    }
}
