package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.PasswordEncoderConfig;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.UsuarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.RolServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un usuario")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {

            Rol rol = rolServiceImpl.buscarPorId(usuarioDTO.getIdRol())
                    .orElseThrow(() -> new RuntimeException("El Rol seleccionado no existe."));

            Usuario usuario = new Usuario();
            String contraseniaEncriptada = passwordEncoderConfig.passwordEncoder().encode(usuarioDTO.getContrasenia());

            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setContrasenia(contraseniaEncriptada);
            usuario.setApellido(usuarioDTO.getApellido());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setRol(rol);

            Usuario nuevoUsuario = usuarioServiceImpl.crear(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(nuevoUsuario));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el usuario.");
        }
    }

    @PutMapping("actualizar")
    @Operation(summary = "Este metodo modifica un usuario (apellido, email, nombre y opcionalmente rol)")
    public ResponseEntity<?> modificarUsuario(@RequestParam Long id, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID del usuario es obligatorio.");
            }

            Optional<Usuario> usuarioOpt = usuarioServiceImpl.buscarPorId(id);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            Usuario u = usuarioOpt.get();

            u.setNombre(usuarioDTO.getNombre());
            u.setApellido(usuarioDTO.getApellido());
            u.setEmail(usuarioDTO.getEmail());

            if (usuarioDTO.getIdRol() != null) {
                Rol rol = rolServiceImpl.buscarPorId(usuarioDTO.getIdRol())
                        .orElseThrow(() -> new RuntimeException("El Rol especificado no existe."));
                u.setRol(rol);
            }

            Usuario usuarioActualizado = usuarioServiceImpl.actualizar(u);

            return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al modificar usuario.");
        }
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un usuario")
    public ResponseEntity<?> eliminarUsuario(@RequestParam Long id) {
        try {
            usuarioServiceImpl.eliminar(id);
            return ResponseEntity.ok().body("Usuario eliminado correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca un usuario")
    public ResponseEntity<?> buscarUsuario(@RequestParam Long id) {
        try {
            Optional<Usuario> usuario = usuarioServiceImpl.buscarPorId(id);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(new UsuarioDTO(usuario.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios() {
        try {
            Optional<List<Usuario>> usuarios = usuarioServiceImpl.buscarTodos();
            if (usuarios.isPresent()) {
                List<UsuarioDTO> dtos = usuarios.get().stream()
                        .map(UsuarioDTO::new)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarPorRol")
    @Operation(summary = "Este metodo busca usuarios por rol")
    public ResponseEntity<?> buscarPorRol(@RequestParam Long idRol) {
        try {
            Optional<Rol> rol = rolServiceImpl.buscarPorId(idRol);
            if (rol.isPresent()) {
                Optional<List<Usuario>> usuarios = usuarioServiceImpl.buscarPorRol(rol.get());
                if (usuarios.isPresent() && !usuarios.get().isEmpty()) {
                    List<UsuarioDTO> dtos = usuarios.get().stream()
                            .map(UsuarioDTO::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(dtos);
                } else {
                    return ResponseEntity.ok(List.of()); // Retorna lista vacía en vez de error si no hay usuarios
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}