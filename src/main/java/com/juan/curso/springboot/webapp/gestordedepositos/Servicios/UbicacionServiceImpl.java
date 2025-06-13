package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ReporteUbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Ubicacion crear(Ubicacion ubicacion) {
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
            throw new RecursoNoEncontradoException("Ubicacion no encontrada con ID: " + ubicacion.getIdUbicacion());
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

    public Ubicacion buscarUbicacionSegunCantidad(int cantidad) {
        try {
            List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();
            for (Ubicacion ubicacion : ubicaciones) {
                if(ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual() >=cantidad) {
                    return ubicacion;
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<ReporteUbicacionDTO> obtenerEspacioDeUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();

        return ubicaciones.stream()
                .map(u -> new ReporteUbicacionDTO(
                        u.getIdUbicacion(),
                        u.getCapacidadMaxima(),
                        u.getOcupadoActual()
                ))
                .sorted(Comparator.comparingInt(ReporteUbicacionDTO::getEspacioUtilizado).reversed())
                .collect(Collectors.toList());
    }

    public Ubicacion obtenerUbicacionConMayorEspacioDisponible() {
        List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();

        return ubicaciones.stream()
                .filter(u -> u.getCapacidadMaxima() >= u.getOcupadoActual())
                .max(Comparator.comparingInt(u -> u.getCapacidadMaxima() - u.getOcupadoActual()))
                .orElse(null);
    }

    public int obtenerCapacidadMaximaDisponibleDeUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionRepositorio.findAll();

        return ubicaciones.stream()
                .mapToInt(u -> u.getCapacidadMaxima() - u.getOcupadoActual())
                .sum();
    }
}
