package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes-recepcion")
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

    @PostMapping("/crear")
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

    @PutMapping("/actualizar")
    @Operation(summary = "Actualiza una orden de recepción completa (cabecera y detalles), ajustando el stock automáticamente.")
    public ResponseEntity<?> actualizarOrdenCompleta(@RequestParam Long id, @RequestBody OrdenRecepcionDTO dto) {
        try {
            OrdenRecepcion actualizada = ordenRecepcionService.procesarModificacionOrden(id, dto);
            return new ResponseEntity<>(new OrdenRecepcionDTO(actualizada), HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al actualizar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina una orden de recepción y revierte el stock ingresado")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            ordenRecepcionService.eliminarConReversion(id);
            return ResponseEntity.ok("Orden eliminada y stock revertido correctamente.");

        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (RuntimeException e) {
            return new ResponseEntity<>("No se puede eliminar: " + e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al eliminar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar-estado")
    @Operation(summary = "Actualiza el estado de la orden")
    public ResponseEntity<?> actualizarEstado(@RequestParam Long id, @RequestParam EstadosDeOrden estado) {
        try {
            ordenRecepcionService.updateOrderState(id, estado);
            return ResponseEntity.ok("Estado actualizado correctamente.");
        } catch (RuntimeException e){
            return new ResponseEntity<>("Error al actualizar el estado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}