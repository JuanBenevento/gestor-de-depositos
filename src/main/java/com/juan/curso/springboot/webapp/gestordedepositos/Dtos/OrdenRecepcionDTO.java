package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosOrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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
    private EstadosOrdenRecepcion estado;
    private List<DetalleRecepcionDTO> detalleRecepcionDTOList;

    public OrdenRecepcionDTO(OrdenRecepcion ordenRecepcion) {
        this.id_orden_recepcion = ordenRecepcion.getIdOrdenRecepcion();
        this.estado = ordenRecepcion.getEstado();
        this.fecha = ordenRecepcion.getFecha();
        this.idProveedor = ordenRecepcion.getProveedor().getId_proveedor();
    }
}
