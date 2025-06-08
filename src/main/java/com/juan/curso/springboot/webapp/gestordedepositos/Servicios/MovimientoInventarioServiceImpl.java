package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;


import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.MovimientoInventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovimientoInventarioServiceImpl implements GenericService<MovimientoInventario, Long> {

    private final MovimientoInventarioRepositorio movimientoInventarioRepositorio;

    @Autowired
    public MovimientoInventarioServiceImpl(MovimientoInventarioRepositorio movimientoInventarioRepositorio) {
        this.movimientoInventarioRepositorio = movimientoInventarioRepositorio;
    }

    @Override
    public Optional<List<MovimientoInventario>> buscarTodos() {
        try{
            return Optional.of(movimientoInventarioRepositorio.findAll());
        }catch(Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<MovimientoInventario> buscarPorId(Long id) {
        try {
            return movimientoInventarioRepositorio.findById(id);
        } catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Movimiento de Inventario no encontrado con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public MovimientoInventario crear(MovimientoInventario movimientoInventario) {
        try {
            movimientoInventario = movimientoInventarioRepositorio.save(movimientoInventario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movimientoInventario;
    }

    @Override
    public MovimientoInventario actualizar(MovimientoInventario movimientoInventario) {
        try {
            movimientoInventario = movimientoInventarioRepositorio.save(movimientoInventario);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Movimiento de Inventario no encontrado con ID: " + movimientoInventario.getId_movimientoInventario());
        }catch (Exception e){
            e.printStackTrace();
        }

        return movimientoInventario;
    }

    @Override
    public void eliminar(Long id) {
        try {
            movimientoInventarioRepositorio.deleteById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Movimiento de Inventario no encontrado con ID: " + id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
