package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.RolRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements GenericService<Rol, Long> {
    @Autowired
    RolRepositorio rolRepositorio;

    public RolServiceImpl() {
    }


    @Override
    public Optional<List<Rol>> buscarTodos() {
        return Optional.empty();
    }

    @Override
    public Optional<Rol> buscarPorId(Long id) {
        Optional<Rol> rol = rolRepositorio.findById(id);
        return rol ;
    }

    @Override
    public void crear(Rol rol) {

    }

    @Override
    public Rol crearConRetorno(Rol rol) {
        rol = rolRepositorio.save(rol);
        return rol;
    }

    @Override
    public Rol actualizar(Rol rol) {
        rol = rolRepositorio.save(rol);
        return rol;
    }

    @Override
    public void eliminar(Long id) {

    }
}
