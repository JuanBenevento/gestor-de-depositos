package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "detalle_recepcion")
public class DetalleRecepcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_detalle_recepcion;

    @ManyToOne
    @JoinColumn (name = "idOrdenRecepcion")
    private OrdenRecepcion ordenRecepcion;

    @ManyToOne
    @JoinColumn (name = "id_producto")
    private Producto producto;

    @NotBlank
    private Double cantidad;
}
