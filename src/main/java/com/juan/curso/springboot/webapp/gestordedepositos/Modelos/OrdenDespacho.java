package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosOrdenRecepcion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orden_despacho")
public class OrdenDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_despacho;
    private Date fecha_despacho;
    @Enumerated(EnumType.STRING)
    private EstadosOrdenRecepcion estado;
    @ManyToOne
    @JoinColumn (name = "id_cliente")
    private Cliente cliente;
    @OneToMany
    @JoinColumn(name = "id_detalle_despacho")
    private List<DetalleDespacho> detalle_despacho;

}
