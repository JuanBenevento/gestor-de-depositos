package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;


import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProveedorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/proveedor")
public class ProveedorController {

    private final ProveedorServiceImpl proveedorService;

    @Autowired
    public ProveedorController(ProveedorServiceImpl proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los proveedores")
    public ResponseEntity<?> buscarTodos() {
        List<ProveedorDTO> proveedores = proveedorService.buscarTodos().orElseThrow().stream().
                map(proveedor -> new ProveedorDTO(
                        proveedor.getId_proveedor(),
                        proveedor.getNombre(),
                        proveedor.getTelefono(),
                        proveedor.getEmail()))
                .collect(Collectors.toList());
        return new ResponseEntity(proveedores, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca un proveedor por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        return new ResponseEntity(new ProveedorDTO(proveedor), HttpStatus.OK);
    }

    @PostMapping("/crearProveedor")
    @Operation(summary = "Este metodo crea un nuevo proveedor")
    public ResponseEntity<?> crear(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.crear(proveedor);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/actualizarProveedor")
    @Operation(summary = "Este metodo actualiza un proveedor")
    public ResponseEntity<?> actualizar(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId_proveedor(dto.getId_proveedor());
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedorService.actualizar(proveedor);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    @Operation(summary = "Este metodo elimina un proveedor por su id")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado con éxito");
    }
}
