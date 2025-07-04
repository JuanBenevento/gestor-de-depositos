package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.OrdenRecepcionRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenRecepcionServiceImpl implements GenericService<OrdenRecepcion, Long>{

    private final OrdenRecepcionRepositorio ordenRecepcionRepositorio;

    @Autowired
    public OrdenRecepcionServiceImpl(OrdenRecepcionRepositorio ordenRecepcionRepositorio) {
        this.ordenRecepcionRepositorio = ordenRecepcionRepositorio;
    }

    @Override
    public Optional<List<OrdenRecepcion>> buscarTodos() {
        Optional<List<OrdenRecepcion>> ordenes = Optional.of(new ArrayList<>(ordenRecepcionRepositorio.findAll()));
        return ordenes;
    }

    @Override
    public Optional<OrdenRecepcion> buscarPorId(Long id) {
        Optional<OrdenRecepcion> orden = ordenRecepcionRepositorio.findById(id);
        return orden;
    }

    @Override
    public OrdenRecepcion crear(OrdenRecepcion ordenRecepcion) {

        try {
            ordenRecepcion = ordenRecepcionRepositorio.save(ordenRecepcion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordenRecepcion;
    }

    @Override
    public OrdenRecepcion actualizar(OrdenRecepcion ordenRecepcion) {
        try {
           ordenRecepcion = ordenRecepcionRepositorio.saveAndFlush(ordenRecepcion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordenRecepcion;
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        try {
            ordenRecepcionRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
