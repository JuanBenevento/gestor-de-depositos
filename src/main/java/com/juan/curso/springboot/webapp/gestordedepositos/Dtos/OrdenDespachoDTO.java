package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
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

    //public OrdenDespachoDTO(Long id_despacho, Date fecha_despacho, EstadosOrdenRecepcion estado, Cliente cliente, List<DetalleDespacho> detalle_despacho) {
    //}
}
