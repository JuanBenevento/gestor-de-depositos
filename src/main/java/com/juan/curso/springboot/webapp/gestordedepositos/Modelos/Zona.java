package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "zona")
public class Zona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zona")
    private Long idZona;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @ElementCollection(targetClass = CategoriasProducto.class)
    @CollectionTable(name = "zona_categorias", joinColumns = @JoinColumn(name = "id_zona"))
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private List<CategoriasProducto> categoriasAdmitidas;
}
