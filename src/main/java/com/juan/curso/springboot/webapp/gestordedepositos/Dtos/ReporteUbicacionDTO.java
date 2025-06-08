package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteUbicacionDTO {

    private Long idUbicacion;
    private int espacioTotal;
    private int espacioUtilizado;
    private int espacioDisponible;


    public ReporteUbicacionDTO(Long idUbicacion, int espacioTotal, int espacioUtilizado) {
        this.idUbicacion = idUbicacion;
        this.espacioTotal = espacioTotal;
        this.espacioUtilizado = espacioUtilizado;
        this.espacioDisponible = espacioTotal - espacioUtilizado;
    }

}
