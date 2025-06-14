package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepositorio extends JpaRepository<Inventario, Long> {
    Inventario findInventarioByProducto_IdProducto(Long idProducto);
    List<Inventario> getInventariosByUbicacion(Ubicacion ubicacion);
    List<Inventario> findAllByProducto_CodigoSku(String codigoSku);
}
