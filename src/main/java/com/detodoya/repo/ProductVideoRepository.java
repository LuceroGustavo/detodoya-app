package com.detodoya.repo;

import com.detodoya.entity.ProductVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVideoRepository extends JpaRepository<ProductVideo, Long> {
    
    // Nota: No se están usando estos métodos ya que ahora usamos ProductImage con isVideo
    // Se mantiene el repositorio por si se necesita en el futuro
    
    Optional<ProductVideo> findById(Long id);
}

