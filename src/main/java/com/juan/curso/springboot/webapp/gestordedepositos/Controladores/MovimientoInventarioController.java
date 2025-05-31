package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;


import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.MovimientoInventarioServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/movimiento-inventario")
public class MovimientoInventarioController {


    private final MovimientoInventarioServiceImpl movimientoInventarioServiceImpl;

    public MovimientoInventarioController(MovimientoInventarioServiceImpl movimientoInventarioServiceImpl) {
        this.movimientoInventarioServiceImpl = movimientoInventarioServiceImpl;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<MovimientoInventarioDTO> movimientosInventario = movimientoInventarioServiceImpl.buscarTodos().orElseThrow().stream().
                map(movInventario -> new MovimientoInventarioDTO(
                        movInventario.getId_movimientoInventario(),
                        movInventario.getProducto(),
                        movInventario.getUbicacionOrigen(),
                        movInventario.getUbicacionDestino(),
                        movInventario.getCantidad(),
                        movInventario.getEstado(),
                        movInventario.getFecha()
                ))
                .collect(Collectors.toList());
        return new ResponseEntity<>(movimientosInventario, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MovimientoInventarioDTO dto) {
        try {
            MovimientoInventario movInventario = new MovimientoInventario(dto.getId_movimiento(), dto.getProducto(), dto.getUbicacion_origen_id(), dto.getUbicacion_destino_id(), dto.getCantidad(), dto.getEstado(), dto.getFecha());

            movimientoInventarioServiceImpl.crear(movInventario);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<MovimientoInventario> movInventarioSelected = movimientoInventarioServiceImpl.buscarPorId(id);
            if (movInventarioSelected.isPresent()) {
                MovimientoInventario movimInvent = movInventarioSelected.get();
                MovimientoInventarioDTO dto = new MovimientoInventarioDTO(movimInvent.getId_movimientoInventario(),movimInvent.getProducto(), movimInvent.getUbicacionOrigen(), movimInvent.getUbicacionDestino(),
                        movimInvent.getCantidad(), movimInvent.getEstado(), movimInvent.getFecha());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Movimiento de inventario no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
