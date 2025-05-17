package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.enums.EstadosOrdenRecepcion;
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
    private Long idOrdenRecepcion;

    @ManyToOne
    @JoinColumn (name = "id_proveedor")
    private Proveedor proveedor;

    @NotBlank
    private Date fecha;

    @Enumerated(EnumType.STRING)
    private EstadosOrdenRecepcion estado;
}
