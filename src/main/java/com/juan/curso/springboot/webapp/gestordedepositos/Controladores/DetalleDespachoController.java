package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
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
    public DetalleDespachoController(DetalleDespachoServiceImpl detalleDespachoServiceImpl, ProductoServiceImpl productoServiceImpl, OrdenDespachoServiceImpl ordenDespachoServiceImpl, InventarioServiceImpl inventarioServiceImpl) {
        this.detalleDespachoServiceImpl = detalleDespachoServiceImpl;
        this.productoServiceImpl = productoServiceImpl;
        this.ordenDespachoServiceImpl = ordenDespachoServiceImpl;
        this.inventarioServiceImpl = inventarioServiceImpl;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<DetalleDespacho> detalles = detalleDespachoServiceImpl.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron detalles de despacho"));

        List<DetalleDespachoDTO> dtoList = detalles.stream()
                .map(detalle -> new DetalleDespachoDTO(
                        detalle.getId_detalle_despacho(),
                        detalle.getOrdenDespacho(),
                        detalle.getProducto(),
                        detalle.getCantidad()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Detalle no encontrada con ID: " + id));
            DetalleDespachoDTO dto = new DetalleDespachoDTO(
                    detalle.getId_detalle_despacho(),
                    detalle.getOrdenDespacho(),
                    detalle.getProducto(),
                    detalle.getCantidad()
            );
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalle", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> crear(@RequestBody DetalleDespachoDTO dto) {
        try {
            Producto producto = productoServiceImpl.buscarPorId(dto.getProducto().getId_producto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            Inventario inventario = inventarioServiceImpl.buscarPorIdProducto(dto.getProducto().getId_producto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

            if (dto.getCantidad() > inventario.getCantidad()) {
                throw new RecursoNoEncontradoException("Cantidad insuficiente en inventario");
            }

            OrdenDespacho orden = ordenDespachoServiceImpl.buscarPorId(dto.getOrdenDespacho().getId_despacho())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setOrdenDespacho(orden);
            detalle.setCantidad(dto.getCantidad());

            DetalleDespacho creado = detalleDespachoServiceImpl.crearConRetorno(detalle);

            DetalleDespachoDTO respuesta = new DetalleDespachoDTO();
            respuesta.setId_detalle_despacho(creado.getId_detalle_despacho());
            respuesta.setProducto(creado.getProducto());
            respuesta.setCantidad(creado.getCantidad());

            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);

        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear detalle de despacho", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO dto) {
        try {
            DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            detalle.setOrdenDespacho(dto.getOrdenDespacho());
            detalle.setProducto(dto.getProducto());
            detalle.setCantidad(dto.getCantidad());

            detalleDespachoServiceImpl.crear(detalle);
            return new ResponseEntity<>(detalle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            detalleDespachoServiceImpl.eliminar(id);
            return new ResponseEntity<>("Orden eliminada con éxito", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
