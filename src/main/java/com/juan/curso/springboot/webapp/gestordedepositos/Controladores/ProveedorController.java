package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;


import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProveedorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorServiceImpl proveedorService;

    @Autowired
    public ProveedorController(ProveedorServiceImpl proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProveedorDTO>> buscarTodos() {
        List<ProveedorDTO> proveedores = proveedorService.buscarTodos().orElseThrow().stream().
                map(proveedor -> new ProveedorDTO(
                        proveedor.getId_proveedor(),
                        proveedor.getNombre(),
                        proveedor.getTelefono(),
                        proveedor.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/buscar")
    public ResponseEntity<ProveedorDTO> buscar(@RequestParam Long id) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        ProveedorDTO proveedorDTO = new ProveedorDTO(
                proveedor.getId_proveedor(),
                proveedor.getNombre(),
                proveedor.getTelefono(),
                proveedor.getEmail()
        );
        return ResponseEntity.ok(proveedorDTO);
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> crear(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.crear(proveedor);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ProveedorDTO> actualizar(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId_proveedor(dto.getId_proveedor());
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.actualizar(proveedor);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado con éxito");
    }
}
