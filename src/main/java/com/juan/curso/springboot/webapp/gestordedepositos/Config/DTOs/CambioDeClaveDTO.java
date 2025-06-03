package com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioDeClaveDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    private String contrasenia;
    @NotBlank
    private String nuevaContrasenia;
}
