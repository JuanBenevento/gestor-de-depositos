package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.AuthServicio;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.DTOs.LoginResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.PasswordEncoderConfig;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.UsuarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.RolServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/GestorDeDepositos/usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioServiceImpl;
    private final RolServiceImpl rolServiceImpl;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Autowired
    public UsuarioController(UsuarioServiceImpl usuarioServiceImpl, RolServiceImpl rolServiceImpl, PasswordEncoderConfig passwordEncoderConfig) {

    this.usuarioServiceImpl = usuarioServiceImpl;
    this.rolServiceImpl = rolServiceImpl;
    this.passwordEncoderConfig = passwordEncoderConfig;

    }

    @PostMapping("/crearUsuario")
    @Operation(summary = "Este metodo crea un usuario")
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

            usuario = usuarioServiceImpl.crear(usuario);
            return ResponseEntity.ok(usuario);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @PutMapping("modificarUsuario")
    @Operation(summary = "Este metodo modifica un usuario (apellido, email y nombre)")
    public ResponseEntity<?> modificarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        Usuario retorno = new Usuario();
        if(usuarioDTO.getIdUsuario() != null){
            Optional<Usuario> usuarioPorID = usuarioServiceImpl.buscarPorId(usuarioDTO.getIdUsuario());
            if( usuarioPorID.isPresent()){
                    usuarioPorID.get().setApellido(usuarioDTO.getApellido());
                    usuarioPorID.get().setEmail(usuarioDTO.getEmail());
                    usuarioPorID.get().setNombre(usuarioDTO.getNombre());
                    retorno =  usuarioServiceImpl.actualizar(usuarioPorID.get());
                }else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

                }
        }

        return ResponseEntity.ok(new UsuarioDTO(retorno));
    }

    @DeleteMapping("/eliminarUsuario")
    @Operation(summary = "Este metodo elimina un usuario")
    public ResponseEntity<?> eliminarUsuario(@RequestParam Long idUsuario) {

            try {
                Optional<Usuario> usuarioPorID = usuarioServiceImpl.buscarPorId(idUsuario);
                if (usuarioPorID.isPresent()) {
                    usuarioServiceImpl.eliminar(usuarioPorID.get().getIdUsuario());
                } else {
                    throw new RecursoNoEncontradoException("El usuario no ha sido encontrado");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscarUsuario")
    @Operation(summary = "Este metodo busca un usuario")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@RequestParam Long idUsuario) {
        try{
            Optional<Usuario> usuarioPorID = usuarioServiceImpl.buscarPorId(idUsuario);
            if (usuarioPorID.isPresent()) {
                return ResponseEntity.ok(new UsuarioDTO(usuarioPorID.get()));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscarTodosLosUsuarios")
    @Operation(summary = "Este metodo busca todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(){
        List<UsuarioDTO> usuariosDTO = new ArrayList<>();
        try{
            Optional<List<Usuario>> usuarios = usuarioServiceImpl.buscarTodos();
            if(usuarios.isPresent()){
                for(Usuario usuario : usuarios.get()){
                    usuariosDTO.add(new UsuarioDTO(usuario));
                }
            }else{
                throw new RecursoNoEncontradoException("No se encontraron usuarios");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/buscarPorRol")
    @Operation(summary = "Este metodo busca usuarios por rol")
    public ResponseEntity<List<UsuarioDTO>> buscarPorRol(@RequestParam Long idRol){
        try{
            Optional<Rol> rol = rolServiceImpl.buscarPorId(idRol);
            if(rol.isPresent()){
                Optional<List<Usuario>> usuariosPorRol = usuarioServiceImpl.buscarPorRol(rol.get());
                if(usuariosPorRol.isPresent()){
                    List<UsuarioDTO> usuariosDTO = usuariosPorRol.get().stream().map(u -> new UsuarioDTO(u)).collect(Collectors.toList());
                    return ResponseEntity.ok(usuariosDTO);
                }else {
                    throw new RecursoNoEncontradoException("No se encontraron usuarios para ese rol");
                }
            }else{
                throw new RecursoNoEncontradoException("No se encontró el rol especificado");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
