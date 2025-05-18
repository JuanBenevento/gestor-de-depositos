package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosOrdenRecepcion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "orden_recepcion")
public class OrdenRecepcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_orden_recepcion;

    @ManyToOne
    @JoinColumn (name = "id_proveedor")
    private Proveedor proveedor;

    private Date fecha;

    @Enumerated(EnumType.STRING)
    private EstadosOrdenRecepcion estado;

    public OrdenRecepcion(Proveedor proveedor, Date fecha, EstadosOrdenRecepcion estado) {
        this.proveedor = proveedor;
        this.fecha = fecha;
        this.estado = estado;
    }
}
