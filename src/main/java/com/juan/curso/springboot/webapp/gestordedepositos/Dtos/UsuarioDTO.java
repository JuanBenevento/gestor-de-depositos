package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String contrasenia;
    private String apellido;
    private String email;
    private Long idRol;
}
