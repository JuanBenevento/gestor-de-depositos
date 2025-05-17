package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orden_despacho")
public class OrdenDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_despacho;
    private Date fecha_despacho;
    private Estado estado;
    @ManyToOne
    @JoinColumn (name = "id_cliente")
    private Cliente cliente;

}
