package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            Optional<Inventario> inventario = inventarioRepositorio.findById(id);
            if (inventario.isEmpty()) {
                throw new RecursoNoEncontradoException("Inventario de despacho con id " + id + " no encontrado");
            }
            return inventario;
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Inventario crear(Inventario inventario) {
        try{
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
            return inventarioRepositorio.save(inventario);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear el registro de inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public Inventario actualizar(Inventario inventario) {
        try{
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
            return inventarioRepositorio.save(inventario);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar el registro de inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try{
            inventarioRepositorio.deleteById(id);
        }catch (RuntimeException e) {
            throw new RuntimeException("Error al eliminar el registro de inventario: " + e.getMessage(), e);
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

    public List<Inventario> buscarPorCodigoSku(String codigoSku) throws RecursoNoEncontradoException {
        Producto producto = productoService.buscarPorCodigoSKU(codigoSku);
        if (producto.getIsDeleted().equals("S")) {
            throw new RecursoNoEncontradoException("Producto no encontrado o eliminado");
        }
        List<Inventario> inventarios = inventarioRepositorio.findAllByProducto_CodigoSku(codigoSku);
        if (inventarios.isEmpty()) {
            throw new RecursoNoEncontradoException("Inventario de producto no encontrado con ese codigo");
        }
        return inventarios;
    }

    public int calcularStockTotalPorIdProducto(Long idProducto) {
        try {
            List<Inventario> inventarios = buscarInventariosPorIdProducto(idProducto);

            int stockTotal = inventarios.stream()
                    .mapToInt(Inventario::getCantidad)
                    .sum();

            return stockTotal;

        } catch (RecursoNoEncontradoException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Error al calcular stock total para el producto " + idProducto + ": " + e.getMessage());
            return 0;
        }
    }

    public int calcularStockTotalPorCodigoSku(String codigoSku) {
        try {
            List<Inventario> inventarios = buscarPorCodigoSku(codigoSku);

            int stockTotal = inventarios.stream().mapToInt(Inventario::getCantidad).sum();
            return stockTotal;
        } catch (RecursoNoEncontradoException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Error al calcular stock total para el product" + codigoSku + ": " + e.getMessage());
            return 0;
        }
    }

    @Transactional
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

    @Transactional
    public List<Inventario> disminuirCantidad(DetalleDespacho detalleDespacho) throws RecursoNoEncontradoException {
        Long idProducto = detalleDespacho.getProducto().getIdProducto();
        int cantidadADescontar = detalleDespacho.getCantidad();

        List<Inventario> inventarios = buscarInventariosPorIdProducto(idProducto);

        int stockTotal = inventarios.stream().mapToInt(Inventario::getCantidad).sum();
        if (stockTotal < cantidadADescontar) {
            throw new StockInsuficienteException("No hay stock suficiente para esta operación. Stock actual: " + stockTotal);
        }

        int cantidadPendiente = cantidadADescontar;

        for (Inventario inventario : inventarios) {
            if (cantidadPendiente <= 0) {
                break;
            }

            int stockEnUbicacion = inventario.getCantidad();

            if (stockEnUbicacion > 0) {
                int cantidadATomar = Math.min(stockEnUbicacion, cantidadPendiente);

                inventario.setCantidad(stockEnUbicacion - cantidadATomar);
                inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                inventarioRepositorio.save(inventario);

                Ubicacion ubicacion = inventario.getUbicacion();
                ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() - cantidadATomar);
                ubicacionRepositorio.save(ubicacion);

                cantidadPendiente -= cantidadATomar;
            }
        }

        return inventarios;
    }

    @Transactional
    public Inventario cambiarUbicacionDeInventario(Long idInventario, Long idNuevaUbicacion){
        try{
            Optional<Inventario> inventarioOpt = inventarioRepositorio.findById(idInventario);
            if (inventarioOpt.isEmpty()){
                throw new RecursoNoEncontradoException("No se encontro el inventario con id: " + idInventario);
            }

            Inventario inventario = inventarioOpt.get();
            Optional<Ubicacion> nuevaUbicacionOpt = ubicacionService.buscarPorId(idNuevaUbicacion);

            if(nuevaUbicacionOpt.isEmpty()){
                throw new RecursoNoEncontradoException("No se encontró ubicación con id: " + idNuevaUbicacion);
            }

            Ubicacion nuevaUbicacion = nuevaUbicacionOpt.get();
            Ubicacion ubicacionAnterior = inventario.getUbicacion();
            int cantidadMovida = inventario.getCantidad();

            int espacioDisponible = nuevaUbicacion.getCapacidadMaxima() - nuevaUbicacion.getOcupadoActual();
            if (espacioDisponible < cantidadMovida) {
                throw new RuntimeException("La nueva ubicación no tiene capacidad suficiente. Requiere: " + cantidadMovida + ", Disponible: " + espacioDisponible);
            }

            inventario.setUbicacion(nuevaUbicacion);
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
            inventarioRepositorio.save(inventario);

            ubicacionAnterior.setOcupadoActual(ubicacionAnterior.getOcupadoActual() - cantidadMovida);
            ubicacionRepositorio.save(ubicacionAnterior);

            nuevaUbicacion.setOcupadoActual(nuevaUbicacion.getOcupadoActual() + cantidadMovida);
            ubicacionRepositorio.save(nuevaUbicacion);

            return inventario;

        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error en el movimiento de inventario: " + e.getMessage(), e);
        }
    }


}