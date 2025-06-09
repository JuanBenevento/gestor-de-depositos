package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ClienteDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ClienteServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/cliente")
public class ClienteController {

    private final ClienteServiceImpl clienteService;

    @Autowired
    public ClienteController(ClienteServiceImpl clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/buscarTodos")
    @Operation(summary = "Este metodo busca todos los clientes guardados en la base de datos")
    public ResponseEntity<?> buscarTodos() {
        List<Cliente> clientes = clienteService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los clientes"));

        List<ClienteDTO> dtoList = clientes.stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca el cliente por ID de tipo Long")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

            return ResponseEntity.ok(new ClienteDTO(cliente));
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar cliente", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearCliente")
    @Operation(summary = "Este metodo crea un nuevo cliente")
    public ResponseEntity<?> crear(@RequestBody ClienteDTO dto) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(dto.getNombre());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(dto.getEmail());

            clienteService.crear(cliente);

            return new ResponseEntity<>(new ClienteDTO(cliente), HttpStatus.CREATED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al crear cliente", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarCliente")
    @Operation(summary = "Este metodo actualiza un cliente")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        try {
            Cliente cliente = clienteService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: "+ id));

            cliente.setNombre(dto.getNombre());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(dto.getEmail());

            clienteService.actualizar(cliente);

            return ResponseEntity.ok(new ClienteDTO(cliente));
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar cliente", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarCliente")
    @Operation(summary = "Este medoto elimina un cliente de la base de datos por id tipo LONG")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            clienteService.eliminar(id);
            return ResponseEntity.ok("Cliente eliminado con éxito");
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar cliente", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
