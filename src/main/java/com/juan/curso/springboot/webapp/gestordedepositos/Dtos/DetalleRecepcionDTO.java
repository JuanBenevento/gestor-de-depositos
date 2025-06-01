package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleRecepcionDTO {

    private Long id_detalle_recepcion;
    private OrdenRecepcion orden;
    private Producto producto;
    private Double cantidad;


    public DetalleRecepcionDTO(Producto producto, @NotNull Double cantidad) {
        this.producto =producto;
        this.cantidad = cantidad;
    }
    public DetalleRecepcionDTO(DetalleRecepcion detalle) {
        this.id_detalle_recepcion = detalle.getIdDetalleRecepcion();
        this.orden = detalle.getOrdenRecepcion();
        this.producto = detalle.getProducto();
        this.cantidad = detalle.getCantidad();
    }
}
