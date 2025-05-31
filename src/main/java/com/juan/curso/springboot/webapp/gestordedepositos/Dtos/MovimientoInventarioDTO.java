package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;


import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioDTO {
    private Long id_movimiento;
    private Producto producto;
    private Ubicacion ubicacion_origen_id;
    private Ubicacion ubicacion_destino_id;
    private Double cantidad;
    private EstadoMovimientoInventario estado;
    private Date fecha;
}
