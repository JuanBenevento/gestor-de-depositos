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
        return Optional.of(zonaRepositorio.findAll());
    }

    @Override
    public Optional<Zona> buscarPorId(Long id) {
        return zonaRepositorio.findById(id);
    }

    @Override
    public void crear(Zona zona) {
        zonaRepositorio.save(zona);
    }

    @Override
    public void actualizar(Zona zona) {
        if (!zonaRepositorio.existsById(zona.getIdZona())) {
            throw new RecursoNoEncontradoException("Zona no encontrado con ID: " + zona.getIdZona());
        }
        zonaRepositorio.save(zona);
    }

    @Override
    public void eliminar(Long id) {
        if (!zonaRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Zona no encontrado con ID: " + id);
        }
        zonaRepositorio.deleteById(id);
    }
}
