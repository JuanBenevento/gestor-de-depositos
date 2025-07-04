package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
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
    @Autowired
    private UbicacionRepositorio ubicacionRepositorio;

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
    public Inventario crear(Inventario inventario) {
        try{
            inventario = inventarioRepositorio.save(inventario);
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

    public List<Inventario> buscarInventariosPorIdProducto(Long idProducto) throws RecursoNoEncontradoException {
        Optional<Producto> producto = productoService.buscarPorId(idProducto);
        if (producto.isEmpty() || producto.get().getIsDeleted().equals("S")) {
            throw new RecursoNoEncontradoException("Producto inexistente o eliminado");
        }
        String sku = producto.get().getCodigoSku();
        return inventarioRepositorio.findAllByProducto_CodigoSku(sku);
    }

//    public void agregarMercaderia(DetalleRecepcionDTO detalle) {
//        Inventario inventario = inventarioRepositorio.findInventarioByProducto_IdProducto(detalle.getProducto().getIdProducto());
//        int cantidadDetalle = detalle.getCantidad();
//
//        if (inventario != null) {
//            Optional<Ubicacion> ubicacionDeProdEncontrada = ubicacionService.buscarPorId(inventario.getUbicacion().getIdUbicacion());
//
//            if (ubicacionDeProdEncontrada.isPresent()) {
//                Ubicacion ubicacionActual = ubicacionDeProdEncontrada.get();
//                int capacidadMaxima = ubicacionActual.getCapacidadMaxima();
//                int cantidadActual = ubicacionActual.getOcupadoActual();
//                int espacioDisponible = capacidadMaxima - cantidadActual;
//
//                if (espacioDisponible >= cantidadDetalle) {
//                    inventario.setCantidad(cantidadActual + cantidadDetalle);
//                    inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
//                    inventarioRepositorio.save(inventario);
//
//                    ubicacionActual.setOcupadoActual(cantidadActual + inventario.getCantidad());
//                    ubicacionService.actualizar(ubicacionActual);
//                } else {
//
//                    int cantidadAColocar = espacioDisponible;
//                    int cantidadRestante = (int) (cantidadDetalle - cantidadAColocar);
//
//                    if (cantidadAColocar > 0) {
//                        inventario.setCantidad(cantidadActual + cantidadAColocar);
//                        inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
//
//                        inventarioRepositorio.save(inventario);
//                        ubicacionActual.setOcupadoActual(cantidadActual + inventario.getCantidad());
//                        ubicacionService.actualizar(ubicacionActual);
//                    }
//
//                    Ubicacion nuevaUbicacionOpt = ubicacionService.buscarUbicacionSegunCantidad(cantidadRestante);
//
//                    if (nuevaUbicacionOpt != null) {
//                        int ocupacionActual = nuevaUbicacionOpt.getOcupadoActual();
//
//                        Inventario nuevoInventario = new Inventario();
//                        nuevoInventario.setProducto(detalle.getProducto());
//                        nuevoInventario.setCantidad(cantidadRestante);
//                        nuevoInventario.setUbicacion(nuevaUbicacionOpt);
//                        nuevoInventario.setFecha_actualizacion(Calendar.getInstance().getTime());
//
//                        inventarioRepositorio.save(nuevoInventario);
//                        nuevaUbicacionOpt.setOcupadoActual(ocupacionActual + cantidadRestante);
//                        ubicacionService.actualizar(nuevaUbicacionOpt);
//                    } else {
//                        throw new RuntimeException("No hay ubicación con capacidad suficiente para la cantidad restante: " + cantidadRestante);
//                    }
//                }
//            }
//        }else{
//            throw new RecursoNoEncontradoException("El producto no está en el inventario");
//        }
//    }

    public void agregarMercaderia(DetalleRecepcionDTO detalle) {
        List<Inventario> inventarios = inventarioRepositorio.findAllByProducto_CodigoSku(detalle.getProducto().getCodigoSku());
        int cantidadRestante = detalle.getCantidad();

        for (Inventario inventario : inventarios) {
            Ubicacion ubicacion = inventario.getUbicacion();
            int espacioDisponible = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();

            if (espacioDisponible > 0) {
                int cantidadAAgregar = Math.min(espacioDisponible, cantidadRestante);
                inventario.setCantidad(inventario.getCantidad() + cantidadAAgregar);
                inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                inventarioRepositorio.save(inventario);

                ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + cantidadAAgregar);
                ubicacionService.actualizar(ubicacion);

                cantidadRestante -= cantidadAAgregar;

                if (cantidadRestante == 0) break;
            }
        }

        // Si aún queda cantidad pendiente, crear nuevo inventario
        if (cantidadRestante > 0) {
            Ubicacion nuevaUbicacion = ubicacionService.buscarUbicacionSegunCantidad(cantidadRestante);

            if (nuevaUbicacion != null) {
                Producto productoPersistido = productoService.buscarPorCodigoSKU(detalle.getProducto().getCodigoSku());

                Inventario nuevoInventario = new Inventario();
                nuevoInventario.setProducto(productoPersistido);
                nuevoInventario.setCantidad(cantidadRestante);
                nuevoInventario.setUbicacion(nuevaUbicacion);
                nuevoInventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                inventarioRepositorio.save(nuevoInventario);

                nuevaUbicacion.setOcupadoActual(nuevaUbicacion.getOcupadoActual() + cantidadRestante);
                ubicacionService.actualizar(nuevaUbicacion);
            } else {
                throw new RuntimeException("No hay ubicación con capacidad suficiente para la cantidad restante: " + cantidadRestante);
            }
        }
    }

    public void disminuirCantidad(DetalleDespacho detalleDespacho){
        try{
            Optional<Producto> productoEncontrado = productoService.buscarPorId(detalleDespacho.getProducto().getIdProducto());
            if (productoEncontrado.isPresent()) {
                List<Inventario> inventario = inventarioRepositorio.findAllByProducto_CodigoSku(productoEncontrado.get().getCodigoSku());
                if(!inventario.isEmpty()){
                    int cantidad = 0;
                    while(cantidad < detalleDespacho.getCantidad()){
                        for(Inventario inventario2 : inventario){
                            cantidad = cantidad + inventario2.getCantidad();
                            inventario2.setCantidad(0);
                            if(cantidad >= detalleDespacho.getCantidad()){
                                break;
                            }
                            inventario2.setFecha_actualizacion(Calendar.getInstance().getTime());
                            inventarioRepositorio.save(inventario2);
                            Ubicacion ubicacion = inventario2.getUbicacion();
                            ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() - detalleDespacho.getCantidad());

                            ubicacionRepositorio.save(ubicacion);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
