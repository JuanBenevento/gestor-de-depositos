package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleDespachoRepositorio extends JpaRepository<DetalleDespacho, Long> {
}
