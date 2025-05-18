package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleRecepcionRepositorio extends JpaRepository<DetalleRecepcion, Long> {
    Optional<List<DetalleRecepcion>> findByOrdenRecepcion_IdOrdenRecepcion(Long idOrden);
}
