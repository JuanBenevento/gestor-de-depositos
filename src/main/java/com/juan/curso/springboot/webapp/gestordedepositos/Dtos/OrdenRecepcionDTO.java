package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
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

    public OrdenRecepcionDTO(OrdenRecepcion ordenRecepcion) {
        this.id_orden_recepcion = ordenRecepcion.getIdOrdenRecepcion();
        this.estado = ordenRecepcion.getEstado();
        this.fecha = ordenRecepcion.getFecha();
        this.idProveedor = ordenRecepcion.getProveedor().getId_proveedor();
    }
}
