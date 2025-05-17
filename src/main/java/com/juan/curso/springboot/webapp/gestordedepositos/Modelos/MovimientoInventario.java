package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import jakarta.persistence.*;
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
@Table(name = "movimiento_inventario")
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_movimientoInventario;
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacionOrigen;
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacionDestino;
    private Double cantidad;
    @Enumerated(EnumType.STRING)
    private EstadoMovimientoInventario estado;
    private Date fecha;

}
