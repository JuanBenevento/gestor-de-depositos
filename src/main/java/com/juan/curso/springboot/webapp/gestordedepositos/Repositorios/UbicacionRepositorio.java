package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Long> {
    List<Ubicacion> findByCapacidadMaximaGreaterThanEqual(int cantidad);
}
