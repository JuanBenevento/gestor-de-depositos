package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.MovimientoInventarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/movimientoInventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioServiceImpl movimientoService;

    @Autowired
    public MovimientoInventarioController(MovimientoInventarioServiceImpl movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Lista todos los movimientos de inventario")
    public ResponseEntity<?> buscarTodos() {
        List<MovimientoInventarioDTO> movimientos = movimientoService.buscarTodos()
                .orElse(List.of())
                .stream()
                .map(MovimientoInventarioDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(movimientos, HttpStatus.OK);
    }

    @PostMapping("/crearMovimiento")
    @Operation(summary = "Crea un movimiento y actualiza el stock en origen y destino")
    public ResponseEntity<?> crear(@RequestBody MovimientoInventarioDTO dto) {
        try {
            // Delegamos la lógica transaccional al servicio
            MovimientoInventario guardado = movimientoService.procesarMovimiento(dto);
            return new ResponseEntity<>(new MovimientoInventarioDTO(guardado), HttpStatus.CREATED);

        } catch (StockInsuficienteException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al procesar el movimiento: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        return movimientoService.buscarPorId(id)
                .map(m -> new ResponseEntity<>(new MovimientoInventarioDTO(m), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina un movimiento y revierte los cambios de stock")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            movimientoService.revertirYEliminar(id);
            return new ResponseEntity<>("Movimiento eliminado y stock revertido correctamente.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}