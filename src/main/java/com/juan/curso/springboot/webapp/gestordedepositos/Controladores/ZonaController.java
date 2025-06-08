package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ZonaDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ZonaServiceImpl;
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

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<ZonaDTO> zonas = zonaService.buscarTodos().orElseThrow().stream().
                map(zona -> new ZonaDTO(
                        zona.getIdZona(),
                        zona.getNombre(),
                        zona.getDescripcion()))
                .collect(Collectors.toList());
        return new ResponseEntity(zonas, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Zona zona = zonaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrado con ID: " + id));

        ZonaDTO zonaDTO = new ZonaDTO(
                zona.getIdZona(),
                zona.getNombre(),
                zona.getDescripcion()
        );
        return new ResponseEntity(zonaDTO, HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody ZonaDTO dto) {
        Zona zona = new Zona();
        zona.setNombre(dto.getNombre());
        zona.setDescripcion(dto.getDescripcion());

        zona = zonaService.crear(zona);
        return new ResponseEntity<>(zona, HttpStatus.CREATED);
    }

    @PutMapping("actualizar")
    public ResponseEntity<?> actualizar(@RequestBody ZonaDTO dto) {
        Zona zona = new Zona();
        zona.setIdZona(dto.getIdZona());
        zona.setNombre(dto.getNombre());
        zona.setDescripcion(dto.getDescripcion());

        zonaService.actualizar(zona);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        zonaService.eliminar(id);
        return ResponseEntity.ok("Zona eliminado con éxito");
    }
}
