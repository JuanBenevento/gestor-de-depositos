package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDespachoDTO {
    private Long idDetalleDespacho;
    private OrdenDespacho ordenDespacho;
    private Producto producto;
    private int cantidad;

}
