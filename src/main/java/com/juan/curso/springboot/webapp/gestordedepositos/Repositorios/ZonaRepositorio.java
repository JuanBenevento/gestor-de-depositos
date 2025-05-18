package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonaRepositorio extends JpaRepository<Zona, Long> {
}
