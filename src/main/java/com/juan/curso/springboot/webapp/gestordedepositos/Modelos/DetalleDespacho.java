package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_detalle_despacho")
    private Long idDetalleDespacho;

    @ManyToOne
    @JoinColumn(name = "id_despacho", nullable = false)
    @JsonIgnore
    private OrdenDespacho ordenDespacho;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;
}
