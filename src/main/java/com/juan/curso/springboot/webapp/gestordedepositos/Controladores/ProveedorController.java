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
@RequestMapping("GestorDeDepositos/proveedor")
public class ProveedorController {

    @Autowired
    ProveedorServiceImpl proveedorService;

    public ProveedorController() {
    }

    @GetMapping("/todos")
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
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        ProveedorDTO proveedorDTO = new ProveedorDTO(
                proveedor.getId_proveedor(),
                proveedor.getNombre(),
                proveedor.getTelefono(),
                proveedor.getEmail()
        );
        return new ResponseEntity(proveedorDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());

        proveedor = proveedorService.crear(proveedor);
        return new ResponseEntity<>(proveedor, HttpStatus.CREATED);
    }

    @PutMapping
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
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado con éxito");
    }
}
