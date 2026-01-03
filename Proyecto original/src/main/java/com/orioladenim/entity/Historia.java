package com.orioladenim.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String titulo;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "video_path", nullable = false)
    private String videoPath;
    
    @Column(name = "video_thumbnail")
    private String videoThumbnail;
    
    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;
    
    @Column(name = "peso_archivo")
    private Long pesoArchivo;
    
    @Column(name = "activa", nullable = false)
    private Boolean activa = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de utilidad
    public String getVideoUrl() {
        return "/uploads/" + videoPath;
    }
    
    public String getThumbnailUrl() {
        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
            // Si la ruta ya incluye thumbnails/, usar directamente
            if (videoThumbnail.startsWith("thumbnails/")) {
                return "/uploads/" + videoThumbnail;
            }
            // Si es solo "historias/...", agregar thumbnails/
            return "/uploads/thumbnails/" + videoThumbnail;
        }
        return "/images/no-video-thumbnail.jpg";
    }
    
    public String getDuracionFormateada() {
        if (duracionSegundos == null) return "0:00";
        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }
    
    public String getPesoFormateado() {
        if (pesoArchivo == null) return "0 MB";
        double mb = pesoArchivo / (1024.0 * 1024.0);
        return String.format("%.1f MB", mb);
    }
    
    // Getters y Setters manuales para evitar problemas con Lombok
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getVideoPath() { return videoPath; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
    
    public String getVideoThumbnail() { return videoThumbnail; }
    public void setVideoThumbnail(String videoThumbnail) { this.videoThumbnail = videoThumbnail; }
    
    public Integer getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(Integer duracionSegundos) { this.duracionSegundos = duracionSegundos; }
    
    public Long getPesoArchivo() { return pesoArchivo; }
    public void setPesoArchivo(Long pesoArchivo) { this.pesoArchivo = pesoArchivo; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
