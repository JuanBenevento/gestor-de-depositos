package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginResponse;
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
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AuthServicio authServicio;
    @Autowired
    PasswordEncoderConfig passwordEncoderConfig;
    public AuthController() {

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


}
