package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProveedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements GenericService<Proveedor, Long> {

    private final ProveedorRepositorio proveedorRepositorio;

    @Autowired
    public ProveedorServiceImpl(ProveedorRepositorio proveedorRepositorio) {
        this.proveedorRepositorio = proveedorRepositorio;
    }

    @Override
    public Optional<List<Proveedor>> buscarTodos() {
        return Optional.of(proveedorRepositorio.findAll());
    }

    @Override
    public Optional<Proveedor> buscarPorId(Long id) {
        return proveedorRepositorio.findById(id);
    }

    @Override
    public void crear(Proveedor proveedor) {
        proveedorRepositorio.save(proveedor);
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        if (!proveedorRepositorio.existsById(proveedor.getId_proveedor())) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedor.getId_proveedor());
        }
        proveedorRepositorio.save(proveedor);
    }

    @Override
    public void eliminar(Long id) {
        if (!proveedorRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id);
        }
        proveedorRepositorio.deleteById(id);
    }
}
