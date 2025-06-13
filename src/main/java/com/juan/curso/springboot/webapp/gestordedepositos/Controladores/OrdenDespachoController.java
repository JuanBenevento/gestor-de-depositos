package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenesDeDespacho")
public class OrdenDespachoController {
    private final OrdenDespachoServiceImpl ordenDespachoService;
    private final ClienteServiceImpl clienteServiceImpl;
    private final ProductoServiceImpl productoServiceImpl;
    private final InventarioServiceImpl inventarioServiceImpl;
    private final UbicacionServiceImpl ubicacionServiceImpl;
    @Autowired
    public OrdenDespachoController(OrdenDespachoServiceImpl ordenDespachoService, ClienteServiceImpl clienteServiceImpl, ProductoServiceImpl productoServiceImpl, InventarioServiceImpl inventarioServiceImpl, UbicacionServiceImpl ubicacionServiceImpl) {
        this.ordenDespachoService = ordenDespachoService;
        this.clienteServiceImpl = clienteServiceImpl;
        this.productoServiceImpl = productoServiceImpl;
        this.inventarioServiceImpl = inventarioServiceImpl;
        this.ubicacionServiceImpl = ubicacionServiceImpl;
    }

    @GetMapping("/buscarTodos")
    @Operation(summary = "Este metodo busca todas las ordenes de despacho")
    public ResponseEntity<?> buscarTodos() {
        List<OrdenDespacho> ordenes = ordenDespachoService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron ordenes de despacho"));

        List<OrdenDespachoDTO> dtoList = ordenes.stream()
                .map(orden -> new OrdenDespachoDTO(
                        orden.getIdDespacho(),
                        orden.getFecha_despacho(),
                        orden.getEstado(),
                        orden.getCliente(),
                        orden.getDetalleDespacho()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId/{id}")
    @Operation(summary = "Este metodo busca una orden de despacho por su id")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            OrdenDespacho orden = ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));
            return new ResponseEntity<>(new OrdenDespachoDTO(orden), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearOrden")
    @Operation(summary = "Este metodo crea una orden de despacho")
    public ResponseEntity<?> crear(@RequestBody OrdenDespachoDTO dto) {
        OrdenDespacho orden = new OrdenDespacho();
        try {
            Optional<Cliente> cliente = clienteServiceImpl.buscarPorId(dto.getCliente().getId_cliente());

            if(cliente.isPresent()) {
                orden.setCliente(cliente.get());
            }else {
                throw new RecursoNoEncontradoException("Cliente no encontrado");
            }

            orden.setFecha_despacho(dto.getFecha_despacho());
            orden.setEstado(dto.getEstado());

            List<DetalleDespacho> detalles = dto.getDetalle_despacho().stream()
                    .map(detalleDto -> {
                        Optional<Producto> producto = productoServiceImpl.buscarPorId(detalleDto.getProducto().getIdProducto());
                        if(producto.isPresent()) {
                            DetalleDespacho detalle = new DetalleDespacho();
                            detalle.setProducto(producto.get());

                            List<Inventario> inventario = inventarioServiceImpl.buscarInventariosPorIdProducto(producto.get().getIdProducto());
                            if(inventario.isEmpty()){
                                new RecursoNoEncontradoException("Inventario no encontrado");
                            }
                            int cantidad = 0;
                            for(Inventario i : inventario) {
                                cantidad = cantidad + i.getCantidad();
                            }
                            if (detalleDto.getCantidad() > cantidad) {
                                throw new RecursoNoEncontradoException("Cantidad insuficiente en inventario");
                            }

                            detalle.setCantidad(detalleDto.getCantidad());
                            detalle.setOrdenDespacho(orden);

                            inventarioServiceImpl.disminuirCantidad(detalle);
                            return detalle;
                        }else {
                            throw new RecursoNoEncontradoException("Producto no encontrado");
                        }
                    })
                    .collect(Collectors.toList());

            orden.setDetalleDespacho(detalles);

            OrdenDespacho retorno = ordenDespachoService.crear(orden);

            return new ResponseEntity<>(new OrdenDespachoDTO(retorno), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Este metodo actualiza una orden de despacho")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody OrdenDespachoDTO ordenDTO) {
        try {
            OrdenDespacho orden = ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            orden.setEstado(ordenDTO.getEstado());

            orden = ordenDespachoService.actualizar(orden);

            return new ResponseEntity<>(new OrdenDespachoDTO(orden), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarOrden")
    @Operation(summary = "Este metodo elimina una orden de despacho por su id")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            if (!ordenDespachoService.ExistePorId(id)) {
                return new ResponseEntity<>("Orden de despacho con id " + id + " no encontrada", HttpStatus.NOT_FOUND);
            }

            Optional<OrdenDespacho> ordenDespacho = ordenDespachoService.buscarPorId(id);
            OrdenDespacho ordenAEliminar = ordenDespacho.get();

            // Reponer productos al inventario
            List<DetalleDespacho> detalles = ordenAEliminar.getDetalleDespacho();
            for (DetalleDespacho detalle : detalles) {
                Producto producto = detalle.getProducto();
                int cantidadAReponer = detalle.getCantidad();

                while (cantidadAReponer > 0) {
                    Ubicacion ubicacionDisponible = ubicacionServiceImpl.obtenerUbicacionConMayorEspacioDisponible();
                    int espacioDisponible = ubicacionDisponible.getCapacidadMaxima() - ubicacionDisponible.getOcupadoActual();

                    if (espacioDisponible <= 0) {
                        throw new RuntimeException("No hay suficiente espacio para reponer los productos.");
                    }

                    int cantidadAColocar = Math.min(espacioDisponible, cantidadAReponer);

                    Inventario inventario = new Inventario();
                    inventario.setProducto(producto);
                    inventario.setCantidad(cantidadAColocar);
                    inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                    inventario.setUbicacion(ubicacionDisponible);
                    inventarioServiceImpl.crear(inventario);

                    ubicacionDisponible.setOcupadoActual(ubicacionDisponible.getOcupadoActual() + cantidadAColocar);
                    ubicacionServiceImpl.actualizar(ubicacionDisponible);

                    cantidadAReponer -= cantidadAColocar;
                }
            }

            ordenDespachoService.eliminar(id);
            return new ResponseEntity<>("Orden eliminada con exito", HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al eliminar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
