package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionCabeceraRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionCabeceraResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    @Operation(summary = "Crea una orden de recepción y actualiza inventario automáticamente")
    public ResponseEntity<?> crearOrdenRecepcion(@RequestBody OrdenRecepcionDTO dto) {
        try {
            OrdenRecepcion creada = ordenRecepcionService.procesarEntradaMercaderia(dto);
            return new ResponseEntity<>(new OrdenRecepcionDTO(creada), HttpStatus.CREATED);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al procesar la orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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