package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProductoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements GenericService<Producto, Long> {

    @Autowired
    ProductoRepositorio productoRepositorio;

    public ProductoServiceImpl() {
    }

    @Override
    public Optional<List<Producto>> buscarTodos() {
        try {
            return Optional.of(productoRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        try {
            return productoRepositorio.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Producto producto) {
        try {
            productoRepositorio.save(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Producto actualizar(Producto producto) {
        try {
          producto=   productoRepositorio.save(producto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return producto;
    }

    @Override
    public void eliminar(Long id) {
        try {
            productoRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public Producto crearConRetorno(Producto producto){
        Producto retorno = new Producto();
        try{
            retorno = productoRepositorio.save(producto);
        }catch(Exception e){
            e.printStackTrace();
        }
        return retorno;
    }

    public Producto buscarPorCodigoSKU(String codigo){
        Producto retorno = new Producto();
        try{
            retorno = productoRepositorio.findProductoByCodigoSkuIs(codigo);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return retorno;
    }
}
