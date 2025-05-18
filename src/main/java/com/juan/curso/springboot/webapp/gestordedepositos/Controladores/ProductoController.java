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
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/producto")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<ProductoDTO> productos = productoService.buscarTodos()
                    .orElseThrow()
                    .stream()
                    .map(producto -> new ProductoDTO(
                            producto.getId_producto(),
                            producto.getNombre(),
                            producto.getDescripcion(),
                            producto.getCodigo_sku(),
                            producto.getUnidad_medida(),
                            producto.getFecha_creacion()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener productos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<Producto> producto = productoService.buscarPorId(id);
            if (producto.isPresent()) {
                Producto p = producto.get();
                ProductoDTO dto = new ProductoDTO(p.getId_producto(), p.getNombre(), p.getDescripcion(),
                        p.getCodigo_sku(), p.getUnidad_medida(), p.getFecha_creacion());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar producto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoDTO dto) {
        try {
            Producto producto = new Producto(dto.getId_producto(), dto.getNombre(), dto.getDescripcion(),
                    dto.getCodigo_sku(), dto.getUnidad_medida(), dto.getFecha_creacion());
            productoService.crear(producto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear producto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody ProductoDTO dto) {
        try {
            Producto producto = new Producto(dto.getId_producto(), dto.getNombre(), dto.getDescripcion(),
                    dto.getCodigo_sku(), dto.getUnidad_medida(), dto.getFecha_creacion());
            productoService.actualizar(producto);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar producto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            productoService.eliminar(id);
            return new ResponseEntity<>("Producto eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar producto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
