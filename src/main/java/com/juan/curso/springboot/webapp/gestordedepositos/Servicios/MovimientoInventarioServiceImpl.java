package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.MovimientoInventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProductoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoInventarioServiceImpl implements GenericService<MovimientoInventario, Long> {

    private final MovimientoInventarioRepositorio movimientoRepositorio;
    private final InventarioRepositorio inventarioRepositorio;
    private final UbicacionRepositorio ubicacionRepositorio;
    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public MovimientoInventarioServiceImpl(
            MovimientoInventarioRepositorio movimientoRepositorio,
            InventarioRepositorio inventarioRepositorio,
            UbicacionRepositorio ubicacionRepositorio,
            ProductoRepositorio productoRepositorio) {
        this.movimientoRepositorio = movimientoRepositorio;
        this.inventarioRepositorio = inventarioRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventario procesarMovimiento(MovimientoInventarioDTO dto) {
        if (dto.getCantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        Producto producto = productoRepositorio.findById(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Ubicacion origen = ubicacionRepositorio.findById(dto.getUbicacionOrigen().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Origen no encontrado"));

        Ubicacion destino = ubicacionRepositorio.findById(dto.getUbicacionDestino().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Destino no encontrado"));

        moverStock(producto, origen, destino, dto.getCantidad());

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUbicacionOrigen(origen);
        movimiento.setUbicacionDestino(destino);
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoMovimientoInventario.REUBICACION);
        movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : new Date());

        return movimientoRepositorio.save(movimiento);
    }

    @Transactional(rollbackFor = Exception.class)
    public void revertirYEliminar(Long idMovimiento) {
        MovimientoInventario movimiento = buscarPorId(idMovimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado"));

        moverStock(
                movimiento.getProducto(),
                movimiento.getUbicacionDestino(),
                movimiento.getUbicacionOrigen(),
                movimiento.getCantidad()
        );
        movimientoRepositorio.delete(movimiento);
    }

    private void moverStock(Producto producto, Ubicacion origen, Ubicacion destino, int cantidad) {

        Inventario invOrigen = buscarInventario(producto, origen)
                .orElseThrow(() -> new StockInsuficienteException("No existe inventario de este producto en el origen"));

        if (invOrigen.getCantidad() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente en origen. Disponible: " + invOrigen.getCantidad());
        }

        invOrigen.setCantidad(invOrigen.getCantidad() - cantidad);
        invOrigen.setFecha_actualizacion(new Date());

        inventarioRepositorio.save(invOrigen);

        int ocupadoOrigen = origen.getOcupadoActual() - cantidad;
        origen.setOcupadoActual(Math.max(0, ocupadoOrigen));
        ubicacionRepositorio.save(origen);

        int espacioDestino = destino.getCapacidadMaxima() - destino.getOcupadoActual();
        if (espacioDestino < cantidad) {
            throw new IllegalArgumentException("La ubicación destino no tiene capacidad suficiente.");
        }

        Inventario invDestino = buscarInventario(producto, destino)
                .orElse(new Inventario());

        if (invDestino.getIdInventario() == null) {
            invDestino.setProducto(producto);
            invDestino.setUbicacion(destino);
            invDestino.setCantidad(cantidad);
        } else {
            invDestino.setCantidad(invDestino.getCantidad() + cantidad);
        }
        invDestino.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(invDestino);

        destino.setOcupadoActual(destino.getOcupadoActual() + cantidad);
        ubicacionRepositorio.save(destino);
    }

    private Optional<Inventario> buscarInventario(Producto producto, Ubicacion ubicacion) {
        List<Inventario> inventarios = inventarioRepositorio.getInventariosByUbicacion(ubicacion);
        return inventarios.stream()
                .filter(i -> i.getProducto().getIdProducto().equals(producto.getIdProducto()))
                .findFirst();
    }

    @Override
    public Optional<List<MovimientoInventario>> buscarTodos() {
        return Optional.of(movimientoRepositorio.findAll());
    }

    @Override
    public Optional<MovimientoInventario> buscarPorId(Long id) {
        return movimientoRepositorio.findById(id);
    }

    @Override
    public MovimientoInventario crear(MovimientoInventario entity) {
        return movimientoRepositorio.save(entity);
    }

    @Override
    public MovimientoInventario actualizar(MovimientoInventario entity) {
        return movimientoRepositorio.save(entity);
    }

    @Override
    public void eliminar(Long id) {
        movimientoRepositorio.deleteById(id);
    }

    @Transactional
    public MovimientoInventario registrarMovimiento(Producto producto, Ubicacion origen, Ubicacion destino, int cantidad, EstadoMovimientoInventario estado) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setUbicacionOrigen(origen);
        mov.setUbicacionDestino(destino);
        mov.setCantidad(cantidad);
        mov.setEstado(estado);
        mov.setFecha(Calendar.getInstance().getTime());
        return movimientoRepositorio.save(mov);
    }
}