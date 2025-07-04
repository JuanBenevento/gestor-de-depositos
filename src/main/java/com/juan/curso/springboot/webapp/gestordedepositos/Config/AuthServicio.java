package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.RolRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServicio  implements UserDetailsService {

    @Autowired
    UsuarioServiceImpl usuarioRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    RolRepositorio rolRepositorio;

    public AuthServicio() {

    }



        @Override
        public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
            Usuario usuario = usuarioRepository.getByNombreEquals(nombre);
            if(usuario.getIdUsuario() == null){
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


}

