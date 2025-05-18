package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

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
}
