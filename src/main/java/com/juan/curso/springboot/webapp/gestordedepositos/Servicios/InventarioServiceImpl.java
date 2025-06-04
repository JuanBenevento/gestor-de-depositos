package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class InventarioServiceImpl implements GenericService<Inventario, Long> {
    @Autowired
    InventarioRepositorio inventarioRepositorio;
    @Autowired
    UbicacionServiceImpl ubicacionService;
    @Autowired
    ProductoServiceImpl productoService;
    public InventarioServiceImpl() {
    }

    @Override
    public Optional<List<Inventario>> buscarTodos() {
        try{
            return Optional.of(inventarioRepositorio.findAll());
        }catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Inventario> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return inventarioRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Inventario de despacho con id " + id + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Inventario inventario) {
        try{
            inventarioRepositorio.save(inventario);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Inventario crearConRetorno(Inventario inventario) {
        try{
            inventario= inventarioRepositorio.save(inventario);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return inventario;
    }

    @Override
    public Inventario actualizar(Inventario inventario) {
        try{
            inventario = inventarioRepositorio.save(inventario);
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return inventario;
    }

    @Override
    public void eliminar(Long id) {
        try{
            inventarioRepositorio.deleteById(id);
        }catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Inventario> buscarPorIdProducto(Long idProducto) throws RecursoNoEncontradoException {
        try {
            Optional<Producto> productoEncontrado = productoService.buscarPorId(idProducto);
            if (productoEncontrado.isPresent() && productoEncontrado.get().getIsDeleted().equals("N")) {
                return Optional.of(inventarioRepositorio.findInventarioByProducto_IdProducto(idProducto));
            }else{
                throw new RecursoNoEncontradoException("No se pudo traer el inventario ya que el producto ya no existe");
            }

        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Inventario del producto con id " + idProducto + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void agregarMercaderia(DetalleRecepcionDTO detalle) {
        Inventario inventario = inventarioRepositorio.findInventarioByProducto_IdProducto(detalle.getProducto().getIdProducto());
        int cantidadDetalle = detalle.getCantidad();

        if (inventario != null) {
            Optional<Ubicacion> ubicacionDeProdEncontrada = ubicacionService.buscarPorId(inventario.getUbicacion().getIdUbicacion());

            if (ubicacionDeProdEncontrada.isPresent()) {
                Ubicacion ubicacionActual = ubicacionDeProdEncontrada.get();
                int capacidadMaxima = ubicacionActual.getCapacidadMaxima();
                int cantidadActual = ubicacionActual.getOcupadoActual();
                int espacioDisponible = capacidadMaxima - cantidadActual;

                if (espacioDisponible >= cantidadDetalle) {
                    inventario.setCantidad(cantidadActual + cantidadDetalle);
                    inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                    inventarioRepositorio.save(inventario);
                } else {
                    int cantidadAColocar = espacioDisponible;
                    int cantidadRestante = (int) (cantidadDetalle - cantidadAColocar);

                    if (cantidadAColocar > 0) {
                        inventario.setCantidad(cantidadActual + cantidadAColocar);
                        inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                        inventarioRepositorio.save(inventario);
                    }

                    Ubicacion nuevaUbicacionOpt = ubicacionService.buscarUbicacionSegunCantidad(cantidadRestante);

                    if (nuevaUbicacionOpt != null) {

                        Inventario nuevoInventario = new Inventario();
                        nuevoInventario.setProducto(detalle.getProducto());
                        nuevoInventario.setCantidad(cantidadRestante);
                        nuevoInventario.setUbicacion(nuevaUbicacionOpt);
                        nuevoInventario.setFecha_actualizacion(Calendar.getInstance().getTime());

                        inventarioRepositorio.save(nuevoInventario);
                    } else {
                        throw new RuntimeException("No hay ubicación con capacidad suficiente para la cantidad restante: " + cantidadRestante);
                    }
                }
            }
        }else{
            throw new RecursoNoEncontradoException("El producto no está en el inventario");
        }
    }

    public Inventario disminuirCantidad(DetalleDespacho detalleDespacho){
        try{
            Optional<Producto> productoEncontrado = productoService.buscarPorId(detalleDespacho.getProducto().getIdProducto());
            if (productoEncontrado.isPresent()) {
                Inventario inventario = inventarioRepositorio.findInventarioByProducto_IdProducto(productoEncontrado.get().getIdProducto());
                inventario.setCantidad(inventario.getCantidad() - detalleDespacho.getCantidad());
                inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                inventarioRepositorio.save(inventario);
                return inventario;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Inventario cambiarUbicacionDeInventario(Long idInventario, Long idNuevaUbicacion){
        try{
            Optional<Inventario> inventario = inventarioRepositorio.findById(idInventario);
            if (inventario.isPresent()){
                Optional<Ubicacion> nuevaUbicacion = ubicacionService.buscarPorId(idNuevaUbicacion);
                if(nuevaUbicacion.isPresent()){
                    inventario.get().setUbicacion(nuevaUbicacion.get());
                    inventarioRepositorio.save(inventario.get());
                    return inventario.get();
                }else{
                    throw new RecursoNoEncontradoException("No se encontró ubicacion con ese id");
                }
            }else{
                throw new RecursoNoEncontradoException("No se encontro el inventario con ese id");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
