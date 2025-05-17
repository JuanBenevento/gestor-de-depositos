package com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.interfaz;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface Repositorio <T, L>{
    public void crear(T t);
    public void eliminar(Long id);
    public Optional<T> buscar(Long id);
    public Optional<List<T>> buscarTodos();
    public void actualizar(T t);
}
