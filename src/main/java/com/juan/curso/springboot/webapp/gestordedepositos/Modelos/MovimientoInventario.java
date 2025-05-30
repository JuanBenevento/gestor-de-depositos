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
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_origen", nullable = false)
    private Ubicacion ubicacionOrigen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_destino", nullable = false)
    private Ubicacion ubicacionDestino;

    private Double cantidad;

    @Enumerated(EnumType.STRING)
    private EstadoMovimientoInventario estado;

    private Date fecha;
}
