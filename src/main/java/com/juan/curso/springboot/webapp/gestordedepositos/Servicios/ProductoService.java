package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.implementacion.ProductoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
@RequestMapping(value = "/GestorDeDepositos/producto")
public class ProductoService {

    @Autowired
    ProductoImpl productoImpl;

    @GetMapping
    public ResponseEntity<?> buscarTodos() {
        HttpStatus status = HttpStatus.OK;
        Optional<List<Producto>> productos = null;
        try {

            productos = productoImpl.buscarTodos();
            if (!productos.isPresent()) {
                status = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(productos, status);
            }
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), status);
        }
        return new ResponseEntity<>(productos.get(), status);
    }

    @GetMapping
    public ResponseEntity<Optional<Producto>> buscar(Long id) {
        HttpStatus status = HttpStatus.OK;
        Optional<Producto> producto = null;
        try{
            producto = productoImpl.buscar(id);
            if (!producto.isPresent()) {
                status = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(producto,status);
            }
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(producto,status);
        }
        return new ResponseEntity<>(producto,status);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {
        HttpStatus status = HttpStatus.CREATED;
        try{
            productoImpl.crear(producto);
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(producto,status);
        }
         return new ResponseEntity<>(producto,status);
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody Producto producto) {
        HttpStatus status = HttpStatus.OK;
        try {
            productoImpl.actualizar(producto);
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(producto,status);
        }
        return new ResponseEntity<>(producto,status);
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(Long id) {
        HttpStatus status = HttpStatus.OK;
        try{
            productoImpl.eliminar(id);
        }catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(status);
        }
        return new ResponseEntity<>(status);
    }

}
