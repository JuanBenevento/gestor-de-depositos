package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private String codigo_sku;
    private String unidad_medida;
    private Date fecha_creacion;


    public ProductoDTO(String nombreProducto,String descripcion ,String unidadMedida, String codigoSku, Date time) {
        this.nombre = nombreProducto;
        this.descripcion = descripcion;
        this.codigo_sku = codigoSku;
        this.unidad_medida = unidadMedida;
        this.fecha_creacion = time;
    }

    public ProductoDTO(Producto producto) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.codigo_sku = producto.getCodigoSku();
        this.unidad_medida = producto.getUnidad_medida();
        this.fecha_creacion = producto.getFecha_creacion();
    }
}
