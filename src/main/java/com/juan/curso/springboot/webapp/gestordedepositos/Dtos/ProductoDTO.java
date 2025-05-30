package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

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
    private Long id_producto;
    private String nombre;
    private String descripcion;
    private Long codigo_sku;
    private String unidad_medida;
    private Date fecha_creacion;
}
