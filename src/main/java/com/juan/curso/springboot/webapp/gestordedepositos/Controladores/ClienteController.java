package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ClienteDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ClienteServiceImpl;
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

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());

        clienteService.crear(cliente);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId_cliente(dto.getId_cliente());
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());

        clienteService.actualizar(cliente);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado con éxito");
    }
}
