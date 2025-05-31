package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleDespachoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/detallesDeDespacho")
public class DetalleDespachoController {
    private final DetalleDespachoServiceImpl detalleDespachoServiceImpl;

    @Autowired
    public DetalleDespachoController(DetalleDespachoServiceImpl detalleDespachoServiceImpl) {
        this.detalleDespachoServiceImpl = detalleDespachoServiceImpl;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<DetalleDespacho> ordenes = detalleDespachoServiceImpl.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron ordenes de despacho"));

        List<com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO> dtoList = ordenes.stream()
                .map(orden -> new com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO(
                        orden.getId_detalle_despacho(),
                        orden.getOrdendespacho(),
                        orden.getProducto(),
                        orden.getCantidad()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            DetalleDespacho orden = detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));
            com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO dto = new com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO(
                    orden.getId_detalle_despacho(),
                    orden.getOrdendespacho(),
                    orden.getProducto(),
                    orden.getCantidad()
            );
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleDespachoDTO dto) {
        try {
            DetalleDespacho detalle = detalleDespachoServiceImpl.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            detalle.setOrdendespacho(dto.getOrdenDespacho());
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
