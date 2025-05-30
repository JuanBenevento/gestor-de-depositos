package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.DetalleRecepcionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleRecepcionServiceImpl implements GenericService<DetalleRecepcion, Long> {
    private final DetalleRecepcionRepositorio detalleRecepcionRepositorio;
    @Autowired
    public DetalleRecepcionServiceImpl (DetalleRecepcionRepositorio detalleRecepcionRepositorio) {
        this.detalleRecepcionRepositorio = detalleRecepcionRepositorio;
    }

    @Override
    public Optional<List<DetalleRecepcion>> buscarTodos() {
        Optional<List<DetalleRecepcion>> detalles = Optional.of(new ArrayList<>(detalleRecepcionRepositorio.findAll()));
        return detalles;
    }
    @Override
    public Optional<DetalleRecepcion> buscarPorId(Long id) {
        Optional<DetalleRecepcion> detalle = detalleRecepcionRepositorio.findById(id);
        return detalle;
    }

    @Override
    public void crear(DetalleRecepcion detalle) {
        try {
            detalleRecepcionRepositorio.save(detalle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actualizar(DetalleRecepcion detalle) {
        try {
            detalleRecepcionRepositorio.save(detalle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            detalleRecepcionRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<List<DetalleRecepcion>> buscarDetallesPorOrden(Long orden) {
        Optional<List<DetalleRecepcion>> detalles = null;
        try{
             detalles = detalleRecepcionRepositorio.findByOrdenRecepcion_IdOrdenRecepcion(orden);
        }catch (Exception e){
            e.printStackTrace();
        }
        return detalles;
    }
}
