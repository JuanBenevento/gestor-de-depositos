package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    public Optional<List<Producto>> buscarTodos();
    public Optional<Producto> buscarPorId(Long id);
    public void crear(Producto producto);
    public void actualizar(Producto producto);
    public void eliminar(Long id);

}
