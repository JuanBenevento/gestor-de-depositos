package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

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
    private Long ordenRecepcion;
    private OrdenRecepcionDTO orden;
    private Producto producto;
    private Long idProducto; // ID del producto; si no existe, se creará
    private String nombreProducto; // Nombre del producto para crearlo si no existe
    private String descripcionProducto;
    private Double cantidad;
    private String codigoSku;
    private String unidadMedida;
    private Date fecha;

    public DetalleRecepcionDTO(Long idDetalleRecepcion, Long ordenRecepcion, Producto producto, @NotNull Double cantidad) {
        this.id_detalle_recepcion = idDetalleRecepcion;
        this.ordenRecepcion = ordenRecepcion;
        this.idProducto =producto.getId_producto();
        this.cantidad = cantidad;
    }
}
