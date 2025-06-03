package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
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
        try {
            return Optional.of(usuarioRepositorio.findAll());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        Optional<Usuario> usuario = null;
       try {
          usuario = Optional.ofNullable(usuarioRepositorio.getUsuarioByIdUsuarioEquals(id));
       }catch (Exception e){
           e.printStackTrace();
       }
        return usuario;
    }

    @Override
    public void crear(Usuario usuario) {
        try{
            usuarioRepositorio.save(usuario);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Usuario crearConRetorno(Usuario usuario) {
        try{
            usuario = usuarioRepositorio.save(usuario);
        }catch (Exception e){
            e.printStackTrace();
        }
        return usuario;
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        try {
            usuario = usuarioRepositorio.save(usuario);
            return usuario;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void eliminar(Long id) {
        try {
            usuarioRepositorio.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Usuario getByNombreEquals(String nombre){
        try {
            return usuarioRepositorio.getByNombreEquals(nombre);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Optional<List<Usuario>> buscarPorRol(Rol rol) {
        try{
        return Optional.ofNullable(usuarioRepositorio.getByRolEquals(rol));

        }catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
