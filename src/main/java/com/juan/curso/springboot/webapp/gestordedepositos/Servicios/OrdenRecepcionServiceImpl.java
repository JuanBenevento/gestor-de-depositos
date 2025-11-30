package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.OrdenRecepcionRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenRecepcionServiceImpl implements GenericService<OrdenRecepcion, Long>{

    private final OrdenRecepcionRepositorio ordenRecepcionRepositorio;
    private final ProveedorServiceImpl proveedorService;
    private final ProductoServiceImpl productoService;
    private final InventarioServiceImpl inventarioService;

    @Autowired
    public OrdenRecepcionServiceImpl(OrdenRecepcionRepositorio ordenRecepcionRepositorio,
                                     ProveedorServiceImpl proveedorService,
                                     ProductoServiceImpl productoService,
                                     InventarioServiceImpl inventarioService) {
        this.ordenRecepcionRepositorio = ordenRecepcionRepositorio;
        this.proveedorService = proveedorService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @Override
    public Optional<List<OrdenRecepcion>> buscarTodos() {
        Optional<List<OrdenRecepcion>> ordenes = Optional.of(new ArrayList<>(ordenRecepcionRepositorio.findAll()));
        return ordenes;
    }

    @Override
    public Optional<OrdenRecepcion> buscarPorId(Long id) {
        Optional<OrdenRecepcion> orden = ordenRecepcionRepositorio.findById(id);
        return orden;
    }

    @Override
    public OrdenRecepcion crear(OrdenRecepcion ordenRecepcion) {

        try {
            ordenRecepcion = ordenRecepcionRepositorio.save(ordenRecepcion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordenRecepcion;
    }

    @Override
    public OrdenRecepcion actualizar(OrdenRecepcion ordenRecepcion) {
        try {
           ordenRecepcion = ordenRecepcionRepositorio.saveAndFlush(ordenRecepcion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordenRecepcion;
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        try {
            ordenRecepcionRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public OrdenRecepcion procesarEntradaMercaderia(OrdenRecepcionDTO dto) {

        Proveedor proveedor = proveedorService.buscarPorId(dto.getProveedor().getId_proveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));

        OrdenRecepcion orden = new OrdenRecepcion();
        orden.setProveedor(proveedor);
        orden.setFecha(Calendar.getInstance().getTime());
        orden.setEstado(dto.getEstado());

        List<DetalleRecepcion> detalles = new ArrayList<>();

        for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
            DetalleRecepcion detalle = new DetalleRecepcion();

            String sku = detalleDTO.getProducto().getCodigoSku();
            Producto producto = productoService.buscarPorCodigoSKU(sku);

            if (producto == null) {
                Producto nuevo = detalleDTO.getProducto();
                nuevo.setIsDeleted("N");
                producto = productoService.crear(nuevo);
            }

            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setOrdenRecepcion(orden);
            detalles.add(detalle);

            inventarioService.agregarMercaderiaConProductoPersistido(producto, detalleDTO.getCantidad());
        }

        orden.setDetallesRecepcion(detalles);

        return ordenRecepcionRepositorio.save(orden);
    }

    @Transactional
    public OrdenRecepcion procesarModificacionOrden(Long idOrden, OrdenRecepcionDTO dto) {
        OrdenRecepcion ordenActual = ordenRecepcionRepositorio.findById(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        if (ordenActual.getEstado() == EstadosDeOrden.COMPLETADA) {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }

        for (DetalleRecepcion detalleViejo : ordenActual.getDetallesRecepcion()) {
            inventarioService.deshacerIngreso(detalleViejo.getProducto(), detalleViejo.getCantidad());
        }

        Proveedor proveedor = proveedorService.buscarPorId(dto.getProveedor().getId_proveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));
        ordenActual.setProveedor(proveedor);
        ordenActual.setFecha(dto.getFecha());
        ordenActual.setEstado(dto.getEstado());

        ordenActual.getDetallesRecepcion().clear();

        ordenRecepcionRepositorio.saveAndFlush(ordenActual);
        List<DetalleRecepcion> nuevosDetalles = new ArrayList<>();
        for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
            DetalleRecepcion detalle = new DetalleRecepcion();

            Producto producto = productoService.buscarPorCodigoSKU(detalleDTO.getProducto().getCodigoSku());
            if (producto == null) {
                Producto nuevo = detalleDTO.getProducto();
                nuevo.setIsDeleted("N");
                producto = productoService.crear(nuevo);
            }

            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setOrdenRecepcion(ordenActual);
            nuevosDetalles.add(detalle);

            inventarioService.agregarMercaderiaConProductoPersistido(producto, detalleDTO.getCantidad());
        }

        ordenActual.getDetallesRecepcion().addAll(nuevosDetalles);

        return ordenRecepcionRepositorio.save(ordenActual);
    }

    @Transactional(rollbackFor = Exception.class)
    public void eliminarConReversion(Long id) {
        OrdenRecepcion orden = ordenRecepcionRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

        // Si la orden ya estaba completada o había impactado stock:
        // Debemos retirar la mercadería que esta orden trajo.
        for (DetalleRecepcion detalle : orden.getDetallesRecepcion()) {

            // Usamos el método que creamos para la edición.
            // Esto busca inventarios de ese producto y los descuenta.
            inventarioService.deshacerIngreso(
                    detalle.getProducto(),
                    detalle.getCantidad()
            );
        }

        // Ahora sí, borramos el registro administrativo
        ordenRecepcionRepositorio.delete(orden);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrderState(Long id, EstadosDeOrden estado) {
        OrdenRecepcion orden = ordenRecepcionRepositorio.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Orden no encontrado"));
        if(orden.getEstado() == EstadosDeOrden.PENDIENTE && estado == EstadosDeOrden.COMPLETADA) {
            orden.setEstado(estado);
        } else {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }
    }
}
