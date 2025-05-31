package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.DetalleDespachoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleDespachoServiceImpl implements GenericService<DetalleDespacho, Long>{
    private final DetalleDespachoRepositorio detalleDespachoRepositorio;

    @Autowired
    public DetalleDespachoServiceImpl(DetalleDespachoRepositorio detalleDespachoRepositorio) {
        this.detalleDespachoRepositorio = detalleDespachoRepositorio;
    }

    @Override
    public Optional<List<DetalleDespacho>> buscarTodos() {
        try {
            return Optional.of(detalleDespachoRepositorio.findAll());
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<DetalleDespacho> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return detalleDespachoRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + id + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(DetalleDespacho detalleDespacho) {
        try {
            detalleDespachoRepositorio.save(detalleDespacho);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(DetalleDespacho detalleDespacho) throws RecursoNoEncontradoException{
        try {
            detalleDespachoRepositorio.save(detalleDespacho);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + detalleDespacho.getId_detalle_despacho() + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            detalleDespachoRepositorio.deleteById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Detalle de despacho con id " + id + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
