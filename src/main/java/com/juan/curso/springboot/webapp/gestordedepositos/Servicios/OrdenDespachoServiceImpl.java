package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.OrdenDespachoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenDespachoServiceImpl implements GenericService<OrdenDespacho, Long>{
    private final OrdenDespachoRepositorio ordenDespachoRepositorio;

    @Autowired
    public OrdenDespachoServiceImpl(OrdenDespachoRepositorio ordenDespachoRepositorio) {
        this.ordenDespachoRepositorio = ordenDespachoRepositorio;
    }

    @Override
    public Optional<List<OrdenDespacho>> buscarTodos() {
        try {
            return Optional.of(ordenDespachoRepositorio.findAll());
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<OrdenDespacho> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return ordenDespachoRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Orden de despacho con id " + id + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public OrdenDespacho crear(OrdenDespacho ordenDespacho) {
        try {
            ordenDespacho = ordenDespachoRepositorio.save(ordenDespacho);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ordenDespacho;
    }

    @Override
    public OrdenDespacho actualizar(OrdenDespacho ordenDespacho) throws RecursoNoEncontradoException{
        try {
            ordenDespacho = ordenDespachoRepositorio.save(ordenDespacho);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Orden de despacho con id " + ordenDespacho.getIdDespacho() + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  ordenDespacho;
    }

    public boolean ExistePorId(Long id) {
        return ordenDespachoRepositorio.existsById(id);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        if (!ordenDespachoRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Orden de despacho con id " + id + " no encontrada");
        }
        try {
            ordenDespachoRepositorio.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la orden con id " + id, e);
        }
    }
}
