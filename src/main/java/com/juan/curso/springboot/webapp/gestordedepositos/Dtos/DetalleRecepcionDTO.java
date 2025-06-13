package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleRecepcionDTO {

    private Long idDetalleRecepcion;
    @JsonIgnore
    private OrdenRecepcion orden;
    private Producto producto;
    private int cantidad;


    public DetalleRecepcionDTO(Producto producto, @NotNull int cantidad) {
        this.producto =producto;
        this.cantidad = cantidad;
    }
    public DetalleRecepcionDTO(DetalleRecepcion detalle) {
        this.idDetalleRecepcion = detalle.getIdDetalleRecepcion();
        this.orden = detalle.getOrdenRecepcion();
        this.producto = detalle.getProducto();
        this.cantidad = detalle.getCantidad();
    }
}
