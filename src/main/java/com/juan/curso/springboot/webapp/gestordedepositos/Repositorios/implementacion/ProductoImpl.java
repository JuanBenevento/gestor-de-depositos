package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.implementacion;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.interfaz.Repositorio;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Optional;

public class ProductoImpl implements Repositorio<Producto, Long> {

    @Override
    public void crear(Producto producto) {

    }

    @Override
    public void eliminar(Long id) {

    }

    @Override
    public Optional<Producto> buscar(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Producto>> buscarTodos() {
        return Optional.empty();
    }

    @Override
    public void actualizar(Producto producto) {

    }
}
