package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes")
public class OrdenRecepcionController {

    private final OrdenRecepcionServiceImpl ordenRecepcionService;
    @Autowired
    public OrdenRecepcionController(OrdenRecepcionServiceImpl ordenRecepcionService) {
        this.ordenRecepcionService = ordenRecepcionService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<OrdenRecepcionDTO> productos = ordenRecepcionService.buscarTodos()
                    .orElseThrow()
                    .stream()
                    .map(orden -> new OrdenRecepcionDTO(
                            orden.getId_orden_recepcion(),
                            orden.getProveedor(),
                            orden.getFecha(),
                            orden.getEstado()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener ordenes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<OrdenRecepcion> orden = ordenRecepcionService.buscarPorId(id);
            if (orden.isPresent()) {
                OrdenRecepcion o = orden.get();
                OrdenRecepcionDTO dto = new OrdenRecepcionDTO(o.getId_orden_recepcion(),o.getProveedor(),
                        o.getFecha(),o.getEstado());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody OrdenRecepcionDTO dto) {
        try {
            OrdenRecepcion producto = new OrdenRecepcion(dto.getProveedor(), Calendar.getInstance().getTime(), dto.getEstado());
            ordenRecepcionService.crear(producto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody OrdenRecepcionDTO ordenDTO) {
        try {
            OrdenRecepcion orden = new OrdenRecepcion(ordenDTO.getId_orden_recepcion(),
                    ordenDTO.getProveedor(),ordenDTO.getFecha(),ordenDTO.getEstado());
            ordenRecepcionService.actualizar(orden);
            return new ResponseEntity<>(ordenDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            ordenRecepcionService.eliminar(id);
            return new ResponseEntity<>("Orden eliminada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
