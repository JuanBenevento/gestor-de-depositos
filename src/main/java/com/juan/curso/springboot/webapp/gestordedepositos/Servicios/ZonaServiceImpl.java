package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ZonaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZonaServiceImpl implements GenericService<Zona, Long>{
    private final ZonaRepositorio zonaRepositorio;

    @Autowired
    public ZonaServiceImpl(ZonaRepositorio zonaRepositorio) {
        this.zonaRepositorio = zonaRepositorio;
    }

    @Override
    public Optional<List<Zona>> buscarTodos() {
        try {
            return Optional.of(zonaRepositorio.findAll());
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Zona> buscarPorId(Long id) throws RecursoNoEncontradoException{
        try {
            return zonaRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Zona no encontrada con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Zona zona) {
        try {
            zonaRepositorio.save(zona);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Zona crearConRetorno(Zona zona) {
        try {
            zona = zonaRepositorio.save(zona);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return zona;
    }

    @Override
    public Zona actualizar(Zona zona) throws RecursoNoEncontradoException {
        try {
           zona = zonaRepositorio.save(zona);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Zona no encontrado con ID: " + zona.getIdZona());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return zona;
    }

    @Override
    public void eliminar(Long id) throws RecursoNoEncontradoException {
        try {
            zonaRepositorio.deleteById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Zona no encontrado con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
