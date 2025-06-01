package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
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
    public ResponseEntity<?> buscarTodos() {
        try {
            List<ProductoDTO> productos = productoServiceImpl.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron productos"))
                    .stream()
                    .map(producto -> new ProductoDTO(
                            producto.getId_producto(),
                            producto.getNombre(),
                            producto.getDescripcion(),
                            String.valueOf(producto.getCodigoSku()), // Convert Long to String
                            producto.getUnidad_medida(),
                            producto.getFecha_creacion()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener productos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<Producto> producto = productoServiceImpl.buscarPorId(id);
            if (producto.isPresent()) {
                Producto p = producto.get();
                ProductoDTO dto = new ProductoDTO(p.getId_producto(), p.getNombre(), p.getDescripcion(),
                        String.valueOf(p.getCodigoSku()), p.getUnidad_medida(), p.getFecha_creacion());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearProducto")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoDTO producto) {
        try {
            Producto producto1 = toEntity(producto);
            producto1.setFecha_creacion(Calendar.getInstance().getTime());
            productoServiceImpl.crear(producto1);
            return new ResponseEntity<>(producto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearConRetorno(@RequestBody ProductoDTO producto) {
        try {
            Producto producto1 = toEntity(producto);
            producto1.setFecha_creacion(Calendar.getInstance().getTime());
            Producto retorno = productoServiceImpl.crearConRetorno(producto1);
            ProductoDTO retornoDTO = toDTO(retorno);
            return new ResponseEntity<>(retornoDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody ProductoDTO dto) {
        try {
            Producto producto = toEntity(dto);
            producto.setFecha_creacion(dto.getFecha_creacion() != null ? dto.getFecha_creacion() : Calendar.getInstance().getTime());
            productoServiceImpl.actualizar(producto);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            productoServiceImpl.eliminar(id);
            return new ResponseEntity<>("Producto eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<ProductoDTO> buscarPorCodigoSKU(String codigo) {
        ProductoDTO dto = null;
        try {
            Optional<Producto> productoEncontrado = Optional.ofNullable(productoServiceImpl.buscarPorCodigoSKU(codigo));
            if (productoEncontrado.isPresent()) {
                dto = toDTO(productoEncontrado.get());
            }
        } catch (NumberFormatException e) {
            System.err.println("Código SKU inválido: " + codigo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(dto);
    }

    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) return null;

        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getId_producto());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCodigo_sku(String.valueOf(producto.getCodigoSku())); // Convert Long to String
        dto.setUnidad_medida(producto.getUnidad_medida());
        dto.setFecha_creacion(producto.getFecha_creacion());
        return dto;
    }

    public Producto toEntity(ProductoDTO dto) {
        if (dto == null) return null;

        Producto producto = new Producto();
        producto.setId_producto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        try {
            producto.setCodigoSku(dto.getCodigo_sku()); // Convert String to Long
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Código SKU inválido: " + dto.getCodigo_sku());
        }
        producto.setUnidad_medida(dto.getUnidad_medida());
        producto.setFecha_creacion(dto.getFecha_creacion());
        return producto;
    }
}