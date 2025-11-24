package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimientoInventario;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_origen", nullable = true)
    private Ubicacion ubicacionOrigen;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_destino", nullable = true)
    private Ubicacion ubicacionDestino;

    private int cantidad;

    private Date fecha;

    @Enumerated(EnumType.STRING)
    private EstadoMovimientoInventario estado;
}