package com.orioladenim.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class Contact {
    
    // Constructor sin argumentos requerido por Hibernate
    public Contact() {
        this.leido = false;
        this.respondido = false;
        this.activo = true;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "email", nullable = false, length = 150)
    private String email;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "asunto", length = 200)
    private String asunto;
    
    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;
    
    @Column(name = "producto_interes")
    private String productoInteres;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "leido", nullable = false)
    private boolean leido = false;
    
    @Column(name = "respondido", nullable = false)
    private boolean respondido = false;
    
    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String respuesta;
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "ubicacion", length = 255)
    private String ubicacion; // Ubicación geográfica del cliente
    
    @Column(name = "activo", nullable = false)
    private boolean activo = true;
    
    // Constructor para crear consulta básica
    public Contact(String nombre, String email, String mensaje) {
        this.nombre = nombre;
        this.email = email;
        this.mensaje = mensaje;
        this.leido = false;
        this.respondido = false;
        this.activo = true;
    }
    
    // Constructor para consulta completa
    public Contact(String nombre, String email, String telefono, String asunto, 
                   String mensaje, String productoInteres) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.productoInteres = productoInteres;
        this.leido = false;
        this.respondido = false;
        this.activo = true;
    }
    
    // Método para marcar como leída
    public void marcarComoLeida() {
        this.leido = true;
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Método para responder
    public void responder(String respuesta) {
        this.respuesta = respuesta;
        this.respondido = true;
        this.fechaRespuesta = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getProductoInteres() { return productoInteres; }
    public void setProductoInteres(String productoInteres) { this.productoInteres = productoInteres; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    
    public boolean isRespondido() { return respondido; }
    public void setRespondido(boolean respondido) { this.respondido = respondido; }
    
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // Métodos para el template
    public String getEstadoFormateado() {
        if (respondido) {
            return "Respondida";
        } else if (leido) {
            return "Leída";
        } else {
            return "Nueva";
        }
    }

    public String getEstadoCssClass() {
        if (respondido) {
            return "badge bg-success";
        } else if (leido) {
            return "badge bg-warning";
        } else {
            return "badge bg-danger";
        }
    }
}
