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
        try {
            return Optional.of(ubicacionRepositorio.findAll());
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Ubicacion> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return ubicacionRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
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
    public Ubicacion crearConRetorno(Ubicacion ubicacion) {
        try {
            ubicacion = ubicacionRepositorio.save(ubicacion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ubicacion;
    }

    @Override
    public Ubicacion actualizar(Ubicacion ubicacion) throws RecursoNoEncontradoException {
        try {
            ubicacion = ubicacionRepositorio.save(ubicacion);
        }catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + ubicacion.getId_ubicacion());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ubicacion;
    }

    @Override
    public void eliminar(Long id) throws RecursoNoEncontradoException {
        try {
            ubicacionRepositorio.deleteById(id);
        }catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
