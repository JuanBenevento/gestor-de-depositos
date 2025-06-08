package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.InventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes")
public class OrdenRecepcionController {

    private final OrdenRecepcionServiceImpl ordenRecepcionService;
    private final ProveedorServiceImpl proveedorServiceImpl;
    private final ProductoServiceImpl productoService;
    private final InventarioServiceImpl inventarioService;
    private final UbicacionServiceImpl ubicacionService;

    @Autowired
    public OrdenRecepcionController(OrdenRecepcionServiceImpl ordenRecepcionService,
    ProveedorServiceImpl proveedorServiceImpl,
    ProductoServiceImpl productoService,
    InventarioServiceImpl inventarioService,
    UbicacionServiceImpl ubicacionService) {

        this.ordenRecepcionService = ordenRecepcionService;
        this.proveedorServiceImpl = proveedorServiceImpl;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
        this.ubicacionService = ubicacionService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todas las ordenes de recepcion")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<OrdenRecepcion> ordenes = ordenRecepcionService.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron órdenes de recepción"));
            List<OrdenRecepcionDTO> ordenesDTO = ordenes.stream()
                    .map(orden -> new OrdenRecepcionDTO(orden))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(ordenesDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener órdenes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca una orden de recepcion por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<OrdenRecepcion> orden = ordenRecepcionService.buscarPorId(id);
            if (orden.isPresent()) {
                OrdenRecepcionDTO dto = new OrdenRecepcionDTO(orden.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearOrdenRecepcion")
    @Operation(summary = "Este metodo crea una nueva orden de recepcion. Si el producto no existe en la base de datos inserta uno nuevo. Valida el proveedor y busca una ubicacion disponible para crear el inventario")
    public ResponseEntity<?> crearOrdenRecepcion(@RequestBody OrdenRecepcionDTO dto) {
        try {
            OrdenRecepcion orden = new OrdenRecepcion();

            // Buscar proveedor
            Proveedor proveedor = proveedorServiceImpl.buscarPorId(dto.getIdProveedor())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            orden.setProveedor(proveedor);
            orden.setFecha(Calendar.getInstance().getTime());
            orden.setEstado(dto.getEstado());

            // Procesar detalles
            List<DetalleRecepcion> detalles = new ArrayList<>();
            for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
                DetalleRecepcion detalle = new DetalleRecepcion();
                Optional<Producto> productoExistente = Optional.ofNullable(productoService.buscarPorCodigoSKU(detalleDTO.getProducto().getCodigoSku()));
                if (productoExistente.isPresent()) {
                    detalle.setProducto(productoExistente.get());
                    Optional<Inventario> inventarioEncontrado = inventarioService.buscarPorIdProducto(productoExistente.get().getIdProducto());
                    inventarioService.agregarMercaderia(detalleDTO);
                } else {

                    Producto retorno =  productoService.crear(detalleDTO.getProducto());
                    detalle.setProducto(retorno);
                    Inventario inventario = new Inventario();
                    inventario.setCantidad(detalleDTO.getCantidad());
                    inventario.setProducto(retorno);
                    inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                    inventario.setUbicacion(ubicacionService.buscarUbicacionSegunCantidad(detalleDTO.getCantidad()));
                    inventarioService.crear(inventario);
                }
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setOrdenRecepcion(orden);
                detalles.add(detalle);

            }

            orden.setDetallesRecepcion(detalles);
            OrdenRecepcion ordenCreada = ordenRecepcionService.crear(orden);
            OrdenRecepcionDTO ordenDTO = new OrdenRecepcionDTO();
            if(ordenCreada!= null && ordenCreada.getDetallesRecepcion() != null) {
                ordenDTO = new OrdenRecepcionDTO(ordenCreada);
                if (ordenDTO != null) {
                    return new ResponseEntity<>(ordenDTO, HttpStatus.CREATED);
                }
            }
            return new ResponseEntity<>(ordenDTO, HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarEstadoOrden")
    @Operation(summary = "Este metodo actualiza el estado de una orden de despacho")
    public ResponseEntity<?> actualizarEstadoOrden(@RequestParam Long idOrden, @RequestParam String estado) {
        try {
            Optional<OrdenRecepcion> existingOrden = ordenRecepcionService.buscarPorId(idOrden);
            if (existingOrden.isEmpty()) {
                return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
            }

            OrdenRecepcion orden = existingOrden.get();
            if(EstadosDeOrden.valueOf(estado) != null) {
                orden.setEstado(EstadosDeOrden.valueOf(estado));
            }else{
                throw new RecursoNoEncontradoException("El estado no es valido");
            }
            orden = ordenRecepcionService.actualizar(orden);

            return new ResponseEntity<>(new OrdenRecepcionDTO(orden), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarOrden")
    @Operation(summary = "Este metodo elimina una orden de recepcion")
    public ResponseEntity<?> eliminar(@RequestParam Long idOrden) {
        try {
            OrdenRecepcion orden = ordenRecepcionService.buscarPorId(idOrden)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            orden.getDetallesRecepcion().clear();
            ordenRecepcionService.eliminar(orden.getIdOrdenRecepcion());
            return new ResponseEntity<>("Orden eliminada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}