package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.AuthServicio;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.PasswordEncoderConfig;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.UsuarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.RolServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/GestorDeDepositos/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;
    @Autowired
    private RolServiceImpl rolServiceImpl;
    @Autowired
    PasswordEncoderConfig passwordEncoderConfig;

    public UsuarioController() {
    }

    @PostMapping("/crearUsuario")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        String contrasenia = passwordEncoderConfig.passwordEncoder().encode(usuarioDTO.getContrasenia());
        Usuario usuario = new Usuario();

        try{
            Rol rol = rolServiceImpl.buscarPorId(usuarioDTO.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setContrasenia(contrasenia);
            usuario.setApellido(usuarioDTO.getApellido());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setRol(rol);

             usuarioServiceImpl.crear(usuario);
            return ResponseEntity.ok(usuario);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }
}
