package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.DetalleDespachoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/detallesDeDespacho")
public class DetalleDespachoController {

    private final DetalleDespachoServiceImpl detalleDespachoServiceImpl;
    private final ProductoServiceImpl productoServiceImpl;
    private final OrdenDespachoServiceImpl ordenDespachoServiceImpl;
    private final InventarioServiceImpl inventarioServiceImpl;

    @Autowired
    public DetalleDespachoController( DetalleDespachoServiceImpl detalleDespachoServiceImpl,
    ProductoServiceImpl productoServiceImpl,
    OrdenDespachoServiceImpl ordenDespachoServiceImpl,
    InventarioServiceImpl inventarioServiceImpl) {
        this.detalleDespachoServiceImpl = detalleDespachoServiceImpl;
        this.productoServiceImpl = productoServiceImpl;
        this.ordenDespachoServiceImpl = ordenDespachoServiceImpl;
        this.inventarioServiceImpl = inventarioServiceImpl;
    }

    @GetMapping("/buscarTodos")
    @Operation(summary = "Este metodo busca todos los deetalles de despachos guardados en la base de datos")
    public ResponseEntity<?> buscarTodos() {
        List<DetalleDespacho> detalles = detalleDespachoServiceImpl.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron detalles de despacho"));

        List<DetalleDespachoDTO> dtoList = detalles.stream()
                .map(DetalleDespachoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId/{id}")
    @Operation(summary = "Este metodo busca un detalle de despacho por el id tipo LONG")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrada con ID: " + id));
            return new ResponseEntity<>( new DetalleDespachoDTO(detalle), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalle", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/crearDetalle")
    @Operation(summary = "Este metodo crea un detalle de despacho. Valida que el producto y el inventario existan como tambien la orden a la que se quiere agregar el detalle")
    public ResponseEntity<?> crear(@RequestBody DetalleDespachoDTO dto) {
        try {
            Producto producto = productoServiceImpl.buscarPorId(dto.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            Inventario inventario = inventarioServiceImpl.buscarPorIdProducto(dto.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

            if (dto.getCantidad() > inventario.getCantidad()) {
                throw new RecursoNoEncontradoException("Cantidad insuficiente en inventario");
            }

            OrdenDespacho orden = ordenDespachoServiceImpl.buscarPorId(dto.getOrdenDespacho().getIdDespacho())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setOrdenDespacho(orden);
            detalle.setCantidad(dto.getCantidad());

            inventarioServiceImpl.disminuirCantidad(detalle);

            detalleDespachoServiceImpl.crear(detalle);

            return new ResponseEntity<>(new DetalleDespachoDTO(detalle), HttpStatus.CREATED);

        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear detalle de despacho", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/actualizarDetalle/{id}")
    @Operation(summary = "Este metodo actualiza un detalle de despacho por el id tipo LONG")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DetalleDespachoDTO dto) {
        try {
            DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrada con ID: " + id));

            Inventario inventario = inventarioServiceImpl.buscarPorIdProducto(dto.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

            if (dto.getCantidad() > inventario.getCantidad()) {
                throw new RecursoNoEncontradoException("Cantidad insuficiente en inventario");
            }

            detalle.setProducto(dto.getProducto());
            detalle.setCantidad(dto.getCantidad());

            detalleDespachoServiceImpl.crear(detalle);
            return new ResponseEntity<>(detalle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar detalle", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarDetalle")
    @Operation(summary = "Este metodo elimina un detalle de una orden de despacho")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            if (!detalleDespachoServiceImpl.ExistePorId(id)) {
                return new ResponseEntity<>("Detalle de despacho con id " + id + " no encontrada", HttpStatus.NOT_FOUND);
            }
            detalleDespachoServiceImpl.eliminar(id);
            return new ResponseEntity<>("Detalle eliminada con exito", HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al eliminar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
