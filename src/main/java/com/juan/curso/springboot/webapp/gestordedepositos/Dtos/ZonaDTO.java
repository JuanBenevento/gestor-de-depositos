package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ZonaDTO {
    private Long idZona;
    private String nombre;
    private String descripcion;

    public ZonaDTO(Zona zona) {
        this.idZona = getIdZona();
        this.nombre = zona.getNombre();
        this.descripcion = zona.getDescripcion();
    }
}
