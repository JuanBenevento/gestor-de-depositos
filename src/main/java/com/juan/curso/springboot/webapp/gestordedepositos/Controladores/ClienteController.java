package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ClienteDTO;
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

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los clientes guardados en la base de datos")
    public ResponseEntity<?> buscarTodos() {
        List<ClienteDTO> clientes = clienteService.buscarTodos().orElseThrow().stream().
                map(cliente -> new ClienteDTO(
                        cliente.getId_cliente(),
                        cliente.getNombre(),
                        cliente.getTelefono(),
                        cliente.getEmail()))
                .collect(Collectors.toList());
        return new ResponseEntity(clientes, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca el cliente por ID de tipo Long")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        ClienteDTO clienteDTO = new ClienteDTO(
                cliente.getId_cliente(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getEmail()
        );
        return new ResponseEntity(clienteDTO, HttpStatus.OK);
    }

    @PostMapping("/crearCliente")
    @Operation(summary = "Este metodo crea un nuevo cliente")
    public ResponseEntity<?> crear(@RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());

        clienteService.crear(cliente);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/actualizarCliente")
    @Operation(summary = "Este metodo actualiza un cliente")
    public ResponseEntity<?> actualizar(@RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId_cliente(dto.getId_cliente());
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());

        clienteService.actualizar(cliente);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/eliminarCliente")
    @Operation(summary = "Este medoto elimina un cliente de la base de datos por id tipo LONG")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok("Cliente eliminado con éxito");
    }
}
