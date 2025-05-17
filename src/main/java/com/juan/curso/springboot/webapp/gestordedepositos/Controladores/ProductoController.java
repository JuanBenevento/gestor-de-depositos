package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/GestorDeDepositos/producto")
public class ProductoController {
    @Autowired
    ProductoService productoService;
    @GetMapping
    public ResponseEntity<?> buscarTodos() {
        List<ProductoDTO> productos = null;
        HttpStatus status = HttpStatus.OK;
        try{
           productos = (List<ProductoDTO>) productoService.buscarTodos().getBody();
            return new ResponseEntity<>(productos, status);

        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(e.getMessage(), status);
        }
    }

    @GetMapping
    public ResponseEntity<Optional<ProductoDTO>> buscar(Long id) {
        HttpStatus status = HttpStatus.OK;
        Optional<ProductoDTO> retorno = null;
        try{
            Optional<Producto> producto  =  productoService.buscar(id).getBody();
            if(producto.isPresent()){
                retorno.get().setId_producto(producto.get().getId_producto());
                retorno.get().setNombre(producto.get().getNombre());
                retorno.get().setDescripcion(producto.get().getDescripcion());
                retorno.get().setCodigo_sku(producto.get().getCodigo_sku());
                retorno.get().setFecha_creacion(producto.get().getFecha_creacion());
            }else{
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(status);
        }
        return new ResponseEntity<>(retorno,status);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {
        HttpStatus status = HttpStatus.CREATED;
        try{
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
        }catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            return new ResponseEntity<>(status);
        }
        return new ResponseEntity<>(status);
    }


}
