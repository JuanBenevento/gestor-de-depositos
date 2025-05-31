package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenDespachoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenesDeDespacho")
public class OrdenDespachoController {
    private final OrdenDespachoServiceImpl ordenDespachoService;

    @Autowired
    public OrdenDespachoController(OrdenDespachoServiceImpl ordenDespachoService) {
        this.ordenDespachoService = ordenDespachoService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<OrdenDespacho> ordenes = ordenDespachoService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron ordenes de despacho"));

        List<OrdenDespachoDTO> dtoList = ordenes.stream()
                .map(orden -> new OrdenDespachoDTO(
                        orden.getId_despacho(),
                        orden.getFecha_despacho(),
                        orden.getEstado(),
                        orden.getCliente(),
                        orden.getDetalle_despacho()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            OrdenDespacho orden = ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));
            OrdenDespachoDTO dto = new OrdenDespachoDTO(
                    orden.getId_despacho(),
                    orden.getFecha_despacho(),
                    orden.getEstado(),
                    orden.getCliente(),
                    orden.getDetalle_despacho()
            );
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>("Orden no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody OrdenDespachoDTO dto) {
        try {
            OrdenDespacho orden = new OrdenDespacho();
            orden.setFecha_despacho(dto.getFecha_despacho());
            orden.setEstado(dto.getEstado());
            orden.setCliente(dto.getCliente());
            orden.setDetalle_despacho(dto.getDetalle_despacho());
            ordenDespachoService.crear(orden);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody OrdenDespachoDTO ordenDTO) {
        try {
            OrdenDespacho orden = ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            orden.setFecha_despacho(ordenDTO.getFecha_despacho());
            orden.setEstado(ordenDTO.getEstado());
            orden.setCliente(ordenDTO.getCliente());
            orden.setDetalle_despacho(ordenDTO.getDetalle_despacho());

            ordenDespachoService.actualizar(orden);

            return new ResponseEntity<>(orden, HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>("Orden no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            ordenDespachoService.eliminar(id);
            return new ResponseEntity<>("Orden eliminada con éxito", HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>("Orden no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
