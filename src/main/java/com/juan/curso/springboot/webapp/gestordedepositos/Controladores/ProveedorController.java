package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;


import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
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

    @GetMapping("/buscarTodos")
    @Operation(summary = "Este metodo busca todos los proveedores")
    public ResponseEntity<?> buscarTodos() {
        List<Proveedor> proveedores = proveedorService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los proveedores"));

        List<ProveedorDTO> dtoList = proveedores.stream()
                .map(ProveedorDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca un proveedor por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Proveedor proveedor = proveedorService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

            return ResponseEntity.ok(new ProveedorDTO(proveedor));
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar proveedor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearProveedor")
    @Operation(summary = "Este metodo crea un nuevo proveedor")
    public ResponseEntity<?> crear(@RequestBody ProveedorDTO dto) {
        try {
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(dto.getNombre());
            proveedor.setTelefono(dto.getTelefono());
            proveedor.setEmail(dto.getEmail());

            proveedorService.crear(proveedor);

            return new ResponseEntity<>(new ProveedorDTO(proveedor), HttpStatus.CREATED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al crear proveedor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarProveedor")
    @Operation(summary = "Este metodo actualiza un proveedor")
    public ResponseEntity<?> actualizar(@RequestBody ProveedorDTO dto) {
        try {
            Proveedor proveedor = proveedorService.buscarPorId(dto.getId_proveedor())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: "+ dto.getId_proveedor()));

            proveedor.setNombre(dto.getNombre());
            proveedor.setTelefono(dto.getTelefono());
            proveedor.setEmail(dto.getEmail());

            proveedorService.actualizar(proveedor);

            return ResponseEntity.ok(new ProveedorDTO(proveedor));
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar proveedor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("eliminarProveedor")
    @Operation(summary = "Este metodo elimina un proveedor por su id")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            proveedorService.eliminar(id);
            return ResponseEntity.ok("Proveedor eliminado con éxito");
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar proveedor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
