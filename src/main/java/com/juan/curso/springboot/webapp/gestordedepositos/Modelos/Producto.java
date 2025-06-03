package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;
    @NotBlank
    @Column(nullable = false)
    private String nombre;
    @NotBlank
    @Column(nullable = false)
    private String descripcion;
    @NotNull
    @Column(nullable = false, name = "codigo_sku")
    private String codigoSku;
    @NotBlank
    @Column(nullable = false)
    private String unidad_medida;
    @NotNull
    @Column(nullable = false)
    private Date fecha_creacion;
    @NotNull
    @Column(name = "is_deleted")
    private String isDeleted;

}
