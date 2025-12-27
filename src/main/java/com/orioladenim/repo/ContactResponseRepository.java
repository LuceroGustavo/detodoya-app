package com.orioladenim.repo;

import com.orioladenim.entity.Contact;
import com.orioladenim.entity.ContactResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactResponseRepository extends JpaRepository<ContactResponse, Long> {
    
    /**
     * Busca todas las respuestas de una consulta ordenadas por fecha (más reciente primero)
     */
    List<ContactResponse> findByContactOrderByFechaRespuestaDesc(Contact contact);
    
    /**
     * Busca todas las respuestas de una consulta por ID
     */
    List<ContactResponse> findByContactIdOrderByFechaRespuestaDesc(Long contactId);
    
    /**
     * Cuenta las respuestas de una consulta
     */
    long countByContact(Contact contact);
}

