package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_inventario;
    @ManyToOne
    @JoinColumn (name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn (name = "id_ubicacion")
    private Ubicacion ubicacion;
    private int cantidad;
    private Date fecha_actualizacion;

}
