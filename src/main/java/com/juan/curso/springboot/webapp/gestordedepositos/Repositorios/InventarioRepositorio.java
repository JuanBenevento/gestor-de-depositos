package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepositorio extends JpaRepository<Inventario, Long> {
    Inventario findInventarioByProducto_IdProducto(Long idProducto);
}
