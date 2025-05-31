package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
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
    private Proveedor proveedor;
    private Date fecha;
    private EstadosDeOrden estado;

    public OrdenRecepcionDTO(Proveedor proveedor, Date time, String upperCase) {
    }

    public OrdenRecepcionDTO(Long idDespacho, java.sql.Date fechaDespacho, EstadosDeOrden estado, Cliente cliente, List<DetalleDespacho> detalleDespacho) {
    }
}
