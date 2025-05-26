package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionServiceImpl implements GenericService<Ubicacion, Long> {
    private final UbicacionRepositorio ubicacionRepositorio;

    @Autowired
    public UbicacionServiceImpl(UbicacionRepositorio ubicacionRepositorio) {
        this.ubicacionRepositorio = ubicacionRepositorio;
    }

    @Override
    public Optional<List<Ubicacion>> buscarTodos() {
        return Optional.of(ubicacionRepositorio.findAll());
    }

    @Override
    public Optional<Ubicacion> buscarPorId(Long id) {
        return ubicacionRepositorio.findById(id);
    }

    @Override
    public void crear(Ubicacion ubicacion) {
        try {
            ubicacionRepositorio.save(ubicacion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        if (!ubicacionRepositorio.existsById(ubicacion.getId_ubicacion())) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + ubicacion.getId_ubicacion());
        }
        ubicacionRepositorio.save(ubicacion);
    }

    @Override
    public void eliminar(Long id) {
        if (!ubicacionRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id);
        }
        ubicacionRepositorio.deleteById(id);
    }
}
