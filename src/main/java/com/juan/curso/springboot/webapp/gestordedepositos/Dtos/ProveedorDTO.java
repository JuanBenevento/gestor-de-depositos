package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorDTO {
    private Long id_proveedor;
    private String nombre;
    private String telefono;
    private String email;

    public ProveedorDTO(Proveedor proveedor) {
        this.id_proveedor = proveedor.getId_proveedor();
        this.nombre = proveedor.getNombre();
        this.telefono = proveedor.getTelefono();
        this.email = proveedor.getEmail();
    }
}
