package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosOrdenRecepcion;
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

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenRecepcionDTO {

    private Long id_orden_recepcion;
    private Proveedor proveedor;
    private Date fecha;
    private EstadosOrdenRecepcion estado;

    public OrdenRecepcionDTO(Proveedor proveedor, Date time, String upperCase) {
    }
}
