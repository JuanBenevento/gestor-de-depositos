package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "ubicacion")
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long idUbicacion;
    @NotBlank
    private String codigo;
    @ManyToOne
    @JoinColumn (name = "id_zona")
    private Zona zona;
    @NotNull
    @Column(name= "capacidad_maxima")
    private int capacidadMaxima;
    @NotNull
    @Column(name = "ocupado_actual")
    private int ocupadoActual;
}
