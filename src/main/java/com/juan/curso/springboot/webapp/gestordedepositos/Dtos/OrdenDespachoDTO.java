package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenDespachoDTO {
    private Long id_despacho;
    private Date fecha_despacho;
    private EstadosDeOrden estado;
    private Cliente cliente;
    private List<DetalleDespacho> detalle_despacho;

    public OrdenDespachoDTO(OrdenDespacho ordenDespacho) {
        this.id_despacho = ordenDespacho.getIdDespacho();
        this.fecha_despacho = ordenDespacho.getFecha_despacho();
        this.estado = ordenDespacho.getEstado();
        this.cliente = ordenDespacho.getCliente();
        this.detalle_despacho = ordenDespacho.getDetalleDespacho();
    }
}
