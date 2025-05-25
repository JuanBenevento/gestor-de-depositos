package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.RolRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UsuarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UsuarioServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServicio  implements UserDetailsService {

        private final UsuarioServiceImpl usuarioRepository;
       // private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
    private final RolRepositorio rolRepositorio;

    public AuthServicio(UsuarioServiceImpl usuarioRepository , JwtUtil jwtUtil, RolRepositorio rolRepositorio) {
            this.usuarioRepository = usuarioRepository;
       //  this.authenticationManager = authenticationManager;
            this.jwtUtil = jwtUtil;
        this.rolRepositorio = rolRepositorio;
    }



        @Override
        public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
            Usuario usuario = usuarioRepository.getByNombreEquals(nombre);
            if(usuario.getId_usuario() == null){
                throw new UsernameNotFoundException("Usuario no encontrado: " + nombre);

            }
            Rol rol = usuario.getRol();
            return org.springframework.security.core.userdetails.User
                    .withUsername(usuario.getNombre())
                    .password(usuario.getContrasenia())
                    .authorities(
                             new SimpleGrantedAuthority("ROLE_"+rol.getNombre())
                    )
                    .build();
        }

        public String generateToken(String nombre){
            return jwtUtil.generateToken(nombre);
        }

        public LoginRequest crearUsuario(Usuario usuario){
            usuarioRepository.crear(usuario);
            Optional<Rol> rol = rolRepositorio.findById(1L);
            usuario.setRol(rol.get());
            return new LoginRequest(usuario.getNombre(), usuario.getContrasenia());
        }

}

