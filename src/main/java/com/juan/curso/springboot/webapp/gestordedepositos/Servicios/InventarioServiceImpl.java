package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioServiceImpl implements GenericService<Inventario, Long> {
    private final InventarioRepositorio inventarioRepositorio;

    @Autowired
    public InventarioServiceImpl(InventarioRepositorio inventarioRepositorio) {
        this.inventarioRepositorio = inventarioRepositorio;
    }

    @Override
    public Optional<List<Inventario>> buscarTodos() {
        return Optional.empty();
    }

    @Override
    public Optional<Inventario> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return inventarioRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Inventario de despacho con id " + id + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Inventario inventario) {

    }

    @Override
    public Inventario actualizar(Inventario inventario) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }

    public Optional<Inventario> buscarPorIdProducto(Long idProducto) throws RecursoNoEncontradoException {
        try {
            return inventarioRepositorio.findByIdProducto(idProducto);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Inventario del producto con id " + idProducto + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
