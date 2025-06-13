package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ClienteDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.UbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ZonaDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ZonaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/zona")
public class ZonaController {
    private final ZonaServiceImpl zonaService;

    @Autowired
    public ZonaController(ZonaServiceImpl zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping("/buscarTodos")
    @Operation(summary = "Este metodo busca todas las zonas")
    public ResponseEntity<?> buscarTodos() {
        List<Zona> zonas = zonaService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron las zonas"));

        List<ZonaDTO> dtoList = zonas.stream()
                .map(ZonaDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca una zona por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Zona zona = zonaService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + id));

            return ResponseEntity.ok(new ZonaDTO(zona));
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar zona", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearZona")
    @Operation(summary = "Este metodo crea una nueva zona")
    public ResponseEntity<?> crear(@RequestBody ZonaDTO dto) {
        try {
            Zona zona = new Zona();
            zona.setNombre(dto.getNombre());
            zona.setDescripcion(dto.getDescripcion());

            zonaService.crear(zona);

            return new ResponseEntity<>(new ZonaDTO(zona), HttpStatus.CREATED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al crear zona", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("actualizarZona")
    @Operation(summary = "Este metodo actualiza una zona")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ZonaDTO dto) {
        try {
            Zona zona = zonaService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Zona no encontrada con id: "+ id));

            zona.setNombre(dto.getNombre());
            zona.setDescripcion(dto.getDescripcion());

            zonaService.actualizar(zona);

            return ResponseEntity.ok(new ZonaDTO(zona));
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar zona", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarZona")
    @Operation(summary = "Este metodo elimina una zona")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            zonaService.eliminar(id);
            return ResponseEntity.ok("Zona eliminada con éxito");
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar zona", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
