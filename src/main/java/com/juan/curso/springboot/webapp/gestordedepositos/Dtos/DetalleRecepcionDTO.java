package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleRecepcionDTO {

    private Long id_detalle_recepcion;
    private OrdenRecepcion ordenRecepcion;
    private Producto producto;
    private Double cantidad;

    public DetalleRecepcionDTO(OrdenRecepcion finalOrden, Producto producto, Double cantidad) {
        this.ordenRecepcion = finalOrden;
        this.producto = producto;
        this.cantidad = cantidad;
    }
}
