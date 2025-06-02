package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Long> {
}
