package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;

import java.util.List;
import java.util.Optional;

public interface GenericService<T, L> {
    public Optional<List<T>> buscarTodos();
    public Optional<T> buscarPorId(Long id);
    public void crear(T t);
    T actualizar(T t);
    public void eliminar(Long id);


}