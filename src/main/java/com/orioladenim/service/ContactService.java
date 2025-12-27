package com.orioladenim.service;

import com.orioladenim.entity.Contact;
import com.orioladenim.entity.ContactResponse;
import com.orioladenim.repo.ContactRepository;
import com.orioladenim.repo.ContactResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private ContactResponseRepository contactResponseRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Métodos básicos CRUD
    public Contact guardar(Contact contact) {
        return contactRepository.save(contact);
    }
    
    public Optional<Contact> buscarPorId(Long id) {
        return contactRepository.findById(id);
    }
    
    public List<Contact> buscarTodos() {
        return contactRepository.findByActivoTrueOrderByFechaCreacionDesc();
    }
    
    public Page<Contact> buscarTodos(Pageable pageable) {
        return contactRepository.findByActivoTrueOrderByFechaCreacionDesc(pageable);
    }
    
    /**
     * Elimina físicamente una consulta y todas sus respuestas de la base de datos.
     * 
     * La eliminación se realiza en dos niveles:
     * 1. Eliminación manual explícita de respuestas (para logs y control)
     * 2. Eliminación en cascada a nivel de base de datos (configurada con @OnDelete)
     * 
     * Esto garantiza que todas las respuestas asociadas se eliminen correctamente,
     * incluso si hay algún problema con la eliminación manual.
     */
    public void eliminar(Long id) {
        Optional<Contact> contact = contactRepository.findById(id);
        if (contact.isPresent()) {
            Contact c = contact.get();
            System.out.println("🗑️ Eliminando consulta ID: " + id);
            System.out.println("   - Nombre: " + c.getNombre());
            System.out.println("   - Respondida: " + c.isRespondido());
            
            // Contar respuestas antes de eliminar (para logs)
            List<ContactResponse> responses = contactResponseRepository.findByContactIdOrderByFechaRespuestaDesc(id);
            int totalRespuestas = responses.size();
            
            if (totalRespuestas > 0) {
                System.out.println("   - Encontradas " + totalRespuestas + " respuesta(s) asociada(s)");
                // Eliminar manualmente todas las respuestas (la cascada de BD también funcionará como respaldo)
                contactResponseRepository.deleteAll(responses);
                System.out.println("   - ✅ " + totalRespuestas + " respuesta(s) eliminada(s) manualmente");
            } else {
                System.out.println("   - No hay respuestas asociadas");
            }
            
            if (c.getRespuesta() != null && !c.getRespuesta().isEmpty()) {
                System.out.println("   - Respuesta legacy será eliminada junto con la consulta");
            }
            
            // Eliminar la consulta (la eliminación en cascada de la BD eliminará cualquier respuesta restante)
            contactRepository.deleteById(id);
            System.out.println("✅ Consulta eliminada correctamente (con eliminación en cascada de respuestas)");
        } else {
            System.out.println("⚠️ Consulta no encontrada con ID: " + id);
        }
    }
    
    // Métodos de estado
    public List<Contact> buscarNoLeidas() {
        return contactRepository.findByLeidoFalseAndActivoTrueOrderByFechaCreacionDesc();
    }
    
    public Page<Contact> buscarNoLeidas(Pageable pageable) {
        return contactRepository.findByLeidoFalseAndActivoTrueOrderByFechaCreacionDesc(pageable);
    }
    
    public List<Contact> buscarNoRespondidas() {
        return contactRepository.findByRespondidoFalseAndActivoTrueOrderByFechaCreacionDesc();
    }
    
    public Page<Contact> buscarNoRespondidas(Pageable pageable) {
        return contactRepository.findByRespondidoFalseAndActivoTrueOrderByFechaCreacionDesc(pageable);
    }
    
    public List<Contact> buscarLeidasNoRespondidas() {
        return contactRepository.findByLeidoTrueAndRespondidoFalseAndActivoTrueOrderByFechaCreacionDesc();
    }
    
    public List<Contact> buscarRespondidas() {
        return contactRepository.findByRespondidoTrueAndActivoTrueOrderByFechaCreacionDesc();
    }
    
    // Métodos de búsqueda
    public List<Contact> buscarPorTermino(String termino) {
        return contactRepository.buscarPorTermino(termino);
    }
    
    public Page<Contact> buscarPorTermino(String termino, Pageable pageable) {
        return contactRepository.buscarPorTermino(termino, pageable);
    }
    
    public List<Contact> buscarPorEmail(String email) {
        return contactRepository.findByEmailAndActivoTrueOrderByFechaCreacionDesc(email);
    }
    
    public List<Contact> buscarPorProducto(String producto) {
        return contactRepository.findByProductoInteresContainingIgnoreCaseAndActivoTrueOrderByFechaCreacionDesc(producto);
    }
    
    // Métodos de filtros
    public List<Contact> buscarConFiltros(Boolean leido, Boolean respondido, 
                                        LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return contactRepository.findConsultasConFiltros(leido, respondido, fechaInicio, fechaFin);
    }
    
    public Page<Contact> buscarConFiltros(Boolean leido, Boolean respondido, 
                                        LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                                        Pageable pageable) {
        return contactRepository.findConsultasConFiltros(leido, respondido, fechaInicio, fechaFin, pageable);
    }
    
    // Métodos de acción
    public void marcarComoLeida(Long id) {
        Optional<Contact> contact = contactRepository.findById(id);
        if (contact.isPresent()) {
            contact.get().marcarComoLeida();
            contactRepository.save(contact.get());
        }
    }
    
    public void responder(Long id, String respuesta) {
        Optional<Contact> contact = contactRepository.findById(id);
        if (contact.isPresent()) {
            Contact c = contact.get();
            
            // Crear nueva respuesta en la tabla de respuestas
            ContactResponse response = new ContactResponse(c, respuesta.trim());
            contactResponseRepository.save(response);
            
            // Mantener compatibilidad: actualizar el campo respuesta en Contact (última respuesta)
            c.setRespuesta(respuesta.trim());
            c.setRespondido(true);
            c.setFechaRespuesta(LocalDateTime.now());
            c.setFechaActualizacion(LocalDateTime.now());
            contactRepository.save(c);
            
            // Enviar respuesta por email al cliente
            try {
                emailService.sendResponseToClient(c);
            } catch (Exception e) {
                System.err.println("Error enviando respuesta al cliente: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene todas las respuestas de una consulta ordenadas por fecha (más reciente primero)
     */
    public List<ContactResponse> obtenerRespuestas(Long contactId) {
        return contactResponseRepository.findByContactIdOrderByFechaRespuestaDesc(contactId);
    }
    
    public void marcarComoLeida(Contact contact) {
        contact.marcarComoLeida();
        contactRepository.save(contact);
    }
    
    public void responder(Contact contact, String respuesta) {
        contact.responder(respuesta);
        contactRepository.save(contact);
    }
    
    // Métodos de estadísticas
    public long contarTotal() {
        return contactRepository.countTotalConsultas();
    }
    
    public long contarNoLeidas() {
        return contactRepository.countConsultasNoLeidas();
    }
    
    public long contarNoRespondidas() {
        return contactRepository.countConsultasNoRespondidas();
    }
    
    public long contarDesde(LocalDateTime fechaInicio) {
        return contactRepository.countConsultasDesde(fechaInicio);
    }
    
    public long contarEntre(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return contactRepository.countConsultasEntre(fechaInicio, fechaFin);
    }
    
    public long contarHoy() {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime finDia = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return contactRepository.countConsultasEntre(inicioDia, finDia);
    }
    
    public long contarEstaSemana() {
        LocalDateTime inicioSemana = LocalDateTime.now().minusDays(7);
        return contactRepository.countConsultasDesde(inicioSemana);
    }
    
    public long contarEsteMes() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return contactRepository.countConsultasDesde(inicioMes);
    }
    
    // Métodos de consultas recientes
    public List<Contact> buscarRecientes(int horas) {
        LocalDateTime fechaInicio = LocalDateTime.now().minusHours(horas);
        return contactRepository.findConsultasRecientes(fechaInicio);
    }
    
    public List<Contact> buscarRecientes() {
        return buscarRecientes(24); // Últimas 24 horas
    }
    
    public List<Contact> buscarPorMes(int año, int mes) {
        return contactRepository.findConsultasPorMes(año, mes);
    }
    
    public List<Contact> buscarPorDia(LocalDateTime fecha) {
        return contactRepository.findConsultasPorDia(fecha);
    }
    
    // Métodos de productos más consultados
    public List<Object[]> buscarProductosMasConsultados() {
        return contactRepository.findProductosMasConsultados();
    }
    
    // Método para crear consulta desde formulario
    public Contact crearConsulta(String nombre, String email, String telefono, 
                                String asunto, String mensaje, String productoInteres,
                                String ipAddress, String userAgent) {
        System.out.println("📝 Creando nueva consulta:");
        System.out.println("   - Nombre: " + nombre);
        System.out.println("   - Email: " + email);
        System.out.println("   - Teléfono: " + (telefono != null ? telefono : "No proporcionado"));
        System.out.println("   - Asunto: " + (asunto != null ? asunto : "No proporcionado"));
        System.out.println("   - Mensaje: " + (mensaje != null && mensaje.length() > 50 ? mensaje.substring(0, 50) + "..." : mensaje));
        System.out.println("   - Producto interés: " + (productoInteres != null ? productoInteres : "No especificado"));
        System.out.println("   - IP: " + ipAddress);
        
        Contact contact = new Contact(nombre, email, telefono, asunto, mensaje, productoInteres);
        contact.setIpAddress(ipAddress);
        contact.setUserAgent(userAgent);
        
        Contact guardado = guardar(contact);
        System.out.println("   ✅ Consulta creada con ID: " + guardado.getId());
        return guardado;
    }
    
    // Método para crear consulta simple
    public Contact crearConsultaSimple(String nombre, String email, String mensaje) {
        Contact contact = new Contact(nombre, email, mensaje);
        return guardar(contact);
    }
    
    // Método para validar email
    public boolean esEmailValido(String email) {
        return email != null && email.contains("@") && email.length() > 5;
    }
    
    // Método para validar consulta
    public boolean esConsultaValida(Contact contact) {
        return contact.getNombre() != null && !contact.getNombre().trim().isEmpty() &&
               contact.getEmail() != null && esEmailValido(contact.getEmail()) &&
               contact.getMensaje() != null && !contact.getMensaje().trim().isEmpty();
    }
    
    // Método para obtener resumen de estadísticas
    public ContactStats obtenerEstadisticas() {
        return new ContactStats(
            contarTotal(),
            contarNoLeidas(),
            contarNoRespondidas(),
            contarHoy(),
            contarEstaSemana(),
            contarEsteMes()
        );
    }
    
    // Clase interna para estadísticas
    public static class ContactStats {
        private final long total;
        private final long noLeidas;
        private final long noRespondidas;
        private final long hoy;
        private final long estaSemana;
        private final long esteMes;
        
        public ContactStats(long total, long noLeidas, long noRespondidas, 
                          long hoy, long estaSemana, long esteMes) {
            this.total = total;
            this.noLeidas = noLeidas;
            this.noRespondidas = noRespondidas;
            this.hoy = hoy;
            this.estaSemana = estaSemana;
            this.esteMes = esteMes;
        }
        
        // Getters
        public long getTotal() { return total; }
        public long getNoLeidas() { return noLeidas; }
        public long getNoRespondidas() { return noRespondidas; }
        public long getHoy() { return hoy; }
        public long getEstaSemana() { return estaSemana; }
        public long getEsteMes() { return esteMes; }
        
        // Métodos calculados
        public long getLeidas() { return total - noLeidas; }
        public long getRespondidas() { return total - noRespondidas; }
        public double getPorcentajeLeidas() { 
            return total > 0 ? (double) getLeidas() / total * 100 : 0; 
        }
        public double getPorcentajeRespondidas() { 
            return total > 0 ? (double) getRespondidas() / total * 100 : 0; 
        }
    }
}

