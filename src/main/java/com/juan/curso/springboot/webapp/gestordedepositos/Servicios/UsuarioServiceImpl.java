package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements GenericService<Usuario, Long> {

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    public UsuarioServiceImpl() {
    }

    @Override
    public Optional<List<Usuario>> buscarTodos() {
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {

        return Optional.empty();
    }

    @Override
    public void crear(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        usuario = usuarioRepositorio.save(usuario);
        return usuario;
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepositorio.deleteById(id);
    }

    public Usuario getByNombreEquals(String nombre){
        return usuarioRepositorio.getByNombreEquals(nombre);
    }
}
