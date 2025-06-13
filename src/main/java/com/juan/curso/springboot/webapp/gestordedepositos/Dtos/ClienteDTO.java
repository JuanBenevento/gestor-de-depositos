package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private Long id_cliente;
    private String nombre;
    private String telefono;
    private String email;

    public ClienteDTO(Cliente cliente) {
        this.id_cliente = cliente.getId_cliente();
        this.nombre = cliente.getNombre();
        this.telefono = cliente.getTelefono();
        this.email = cliente.getEmail();
    }
}
