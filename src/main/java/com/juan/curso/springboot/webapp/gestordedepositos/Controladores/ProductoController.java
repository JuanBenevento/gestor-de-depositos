package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/producto")
public class ProductoController {

    private final ProductoServiceImpl productoServiceImpl;

    @Autowired
    public ProductoController(ProductoServiceImpl productoServiceImpl) {
        this.productoServiceImpl = productoServiceImpl;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los productos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<ProductoDTO> productos = productoServiceImpl.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron productos"))
                    .stream()
                    .map(ProductoDTO::new).filter(p->p.getIsDeleted().equals("N"))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener productos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca un producto por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<Producto> producto = productoServiceImpl.buscarPorId(id);
            if (producto.isPresent() && producto.get().getIsDeleted().equals("N")) {
                ProductoDTO dto = new ProductoDTO(producto.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un nuevo producto")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoDTO productoDTO) {
        try {
            Producto productoEntity = toEntity(productoDTO);
            productoEntity.setFecha_creacion(Calendar.getInstance().getTime());

            productoEntity = productoServiceImpl.crear(productoEntity);
            return new ResponseEntity<>(productoEntity, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un producto")
    public ResponseEntity<?> actualizar(@RequestParam Long id, @RequestBody ProductoDTO dto) {
        try {
            Producto producto = toEntity(dto);
            producto.setFecha_creacion(dto.getFecha_creacion() != null ? dto.getFecha_creacion() : Calendar.getInstance().getTime());

            producto = productoServiceImpl.actualizar(producto);
            return new ResponseEntity<>(producto, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Producto toEntity(ProductoDTO dto) {
        if (dto == null) return null;

        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCategoria(dto.getCategoria());

        try {
            producto.setCodigoSku(dto.getCodigoSku());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Código SKU inválido: " + dto.getCodigoSku());
        }
        producto.setUnidad_medida(dto.getUnidad_medida());
        producto.setFecha_creacion(dto.getFecha_creacion());
        producto.setIsDeleted("N");
        return producto;
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un producto")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            productoServiceImpl.eliminar(id);
            return new ResponseEntity<>("Producto eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/buscarPorCodigoSku")
    @Operation(summary = "Este metodo busca producto por su codigo sku")
    public ResponseEntity<?> buscarPorCodigoSKU(@RequestParam String codigo) {
        try {
            Producto producto = productoServiceImpl.buscarPorCodigoSKU(codigo);

            if (producto == null || producto.getIsDeleted().equals("S")) {
                return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(new ProductoDTO(producto), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar producto: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/buscarPorNombreOCodigo")
    @Operation(summary = "Busca productos por nombre o código SKU")
    public ResponseEntity<?> buscarPorNombreOCodigo(@RequestParam String valor) {
        try {
            List<ProductoDTO> productos = productoServiceImpl.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron productos"))
                    .stream()
                    .filter(p -> p.getIsDeleted().equals("N") &&
                            (p.getNombre().toLowerCase().contains(valor.toLowerCase()) ||
                                    p.getCodigoSku().toLowerCase().contains(valor.toLowerCase())))
                    .map(ProductoDTO::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar productos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}