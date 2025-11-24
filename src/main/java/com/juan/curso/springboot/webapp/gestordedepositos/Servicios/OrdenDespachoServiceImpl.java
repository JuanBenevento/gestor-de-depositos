package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.OrdenDespachoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenDespachoServiceImpl implements GenericService<OrdenDespacho, Long>{
    private final OrdenDespachoRepositorio ordenDespachoRepositorio;
    private final ClienteServiceImpl clienteService;
    private final ProductoServiceImpl productoService;
    private final InventarioServiceImpl inventarioService;

    @Autowired
    public OrdenDespachoServiceImpl(OrdenDespachoRepositorio ordenDespachoRepositorio,
                                    ClienteServiceImpl clienteService,
                                    ProductoServiceImpl productoService,
                                    InventarioServiceImpl inventarioService) {
        this.ordenDespachoRepositorio = ordenDespachoRepositorio;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @Override
    public Optional<List<OrdenDespacho>> buscarTodos() {
        try {
            return Optional.of(ordenDespachoRepositorio.findAll());
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<OrdenDespacho> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return ordenDespachoRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Orden de despacho con id " + id + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public OrdenDespacho crear(OrdenDespacho ordenDespacho) {
        try {
            ordenDespacho = ordenDespachoRepositorio.save(ordenDespacho);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ordenDespacho;
    }

    @Override
    public OrdenDespacho actualizar(OrdenDespacho ordenDespacho) throws RecursoNoEncontradoException{
        try {
            ordenDespacho = ordenDespachoRepositorio.save(ordenDespacho);
        }catch (RecursoNoEncontradoException e){
            throw new RecursoNoEncontradoException("Orden de despacho con id " + ordenDespacho.getIdOrdenDespacho() + " no encontrado");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  ordenDespacho;
    }

    public boolean ExistePorId(Long id) {
        return ordenDespachoRepositorio.existsById(id);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        if (!ordenDespachoRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Orden de despacho con id " + id + " no encontrada");
        }
        try {
            ordenDespachoRepositorio.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la orden con id " + id, e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public OrdenDespacho procesarSalidaMercaderia(OrdenDespachoDTO dto) {

        // 1. Validar Cliente
        Cliente cliente = clienteService.buscarPorId(dto.getCliente().getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        OrdenDespacho orden = new OrdenDespacho();
        orden.setCliente(cliente);
        orden.setFechaDespacho(dto.getFechaDespacho());
        orden.setEstado(dto.getEstado());

        List<DetalleDespacho> detallesEntity = new ArrayList<>();

        // 2. Procesar cada producto
        for (DetalleDespacho detalleDto : dto.getDetalle_despacho()) {

            // Validar Producto
            Producto producto = productoService.buscarPorId(detalleDto.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado (ID: " + detalleDto.getProducto().getIdProducto() + ")"));

            int cantidadSolicitada = detalleDto.getCantidad();

            // 3. Validar Stock Total
            int stockTotal = inventarioService.calcularStockTotalPorIdProducto(producto.getIdProducto());
            if (stockTotal < cantidadSolicitada) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre() +
                        ". Solicitado: " + cantidadSolicitada + ", Disponible: " + stockTotal);
            }

            // 4. Crear Detalle
            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidadSolicitada);
            detalle.setOrdenDespacho(orden);
            detallesEntity.add(detalle);

            // 5. Descontar del Inventario (FIFO o lógica interna)
            // Este método ya lo tienes en InventarioService, asegúrate que lance excepciones si falla
            inventarioService.disminuirCantidad(detalle);
        }

        orden.setDetalleDespacho(detallesEntity);
        return ordenDespachoRepositorio.save(orden);
    }
}
