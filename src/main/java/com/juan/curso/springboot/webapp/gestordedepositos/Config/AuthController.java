package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/GestorDeDepositos")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthServicio authServicio;
    @Autowired
    PasswordEncoderConfig passwordEncoderConfig;
    public AuthController(AuthenticationManager authenticationManager, AuthServicio authServicio) {
        this.authenticationManager = authenticationManager;
        this.authServicio = authServicio;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getNombre(), loginRequest.getContrasenia()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails usuario =  authServicio.loadUserByUsername(loginRequest.getNombre());
        if(usuario.getPassword() == null){
            throw new UsernameNotFoundException("Usuario no encontrado: " + loginRequest.getNombre());

        }
        String token = authServicio.generateToken(usuario.getUsername());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(usuario.getUsername());
        response.setRol(usuario.getAuthorities().iterator().next().getAuthority());
        return response;
    }
    @PostMapping("/crearUsuario")
    public LoginResponse crearLogin(@RequestBody LoginRequest loginRequest) {
        String nombre = loginRequest.getNombre();
        String contrasenia = passwordEncoderConfig.passwordEncoder().encode(loginRequest.getContrasenia());

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setContrasenia(contrasenia);
        LoginRequest request = authServicio.crearUsuario(usuario);
        return this.login(request);

    }

}
