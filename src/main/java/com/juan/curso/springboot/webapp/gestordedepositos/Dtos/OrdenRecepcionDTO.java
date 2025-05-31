package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenRecepcionDTO {

    private Long id_orden_recepcion;
    private Long idProveedor;
    private Date fecha;
    private EstadosDeOrden estado;
    private List<DetalleRecepcionDTO> detalleRecepcionDTOList;

}
