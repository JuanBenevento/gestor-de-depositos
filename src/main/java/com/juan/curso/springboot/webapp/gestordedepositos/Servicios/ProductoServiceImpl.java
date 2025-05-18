package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements GenericService<Producto, Long> {

    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public ProductoServiceImpl(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    @Override
    public Optional<List<Producto>> buscarTodos() {
        try {
            return Optional.of(productoRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        try {
            return productoRepositorio.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Producto producto) {
        try {
            productoRepositorio.save(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Producto producto) {
        try {
            productoRepositorio.save(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            productoRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
