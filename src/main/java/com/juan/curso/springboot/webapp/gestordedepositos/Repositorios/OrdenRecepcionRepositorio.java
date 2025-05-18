package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRecepcionRepositorio extends JpaRepository<OrdenRecepcion, Long> {

}
