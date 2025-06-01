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
        try{
            return Optional.of(proveedorRepositorio.findAll());
        }catch(Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Proveedor> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return proveedorRepositorio.findById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Proveedor proveedor) {
        try {
            proveedorRepositorio.save(proveedor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Proveedor actualizar(Proveedor proveedor) throws RecursoNoEncontradoException {
        try {
            proveedor = proveedorRepositorio.save(proveedor);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedor.getId_proveedor());
        }catch (Exception e){
            e.printStackTrace();
        }
        return proveedor;
    }

    @Override
    public void eliminar(Long id) {
        try {
            proveedorRepositorio.deleteById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}