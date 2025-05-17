package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "detalle_despacho")
public class DetalleDespacho {

    @Id
    private Long id_detalle_despacho;
    @ManyToOne
    @JoinColumn(name = "id_despacho")
    private OrdenDespacho Ordendespacho;
    @OneToOne
    @JoinColumn(name="id_producto")
    private Producto producto;
    private Integer cantidad;
}
