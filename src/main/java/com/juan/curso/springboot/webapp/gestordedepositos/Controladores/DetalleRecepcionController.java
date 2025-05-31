package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenRecepcionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/detalleRecepcion")
public class DetalleRecepcionController {
    private final DetalleRecepcionServiceImpl detalleRecepcionService;
    private final OrdenRecepcionServiceImpl ordenRecepcionService; // Added service dependency
    @Autowired
    ProductoController productoController;

    @Autowired
    public DetalleRecepcionController(DetalleRecepcionServiceImpl detalleRecepcionService,
                                      OrdenRecepcionServiceImpl ordenRecepcionService) {
        this.detalleRecepcionService = detalleRecepcionService;
        this.ordenRecepcionService = ordenRecepcionService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<DetalleRecepcionDTO> productos = detalleRecepcionService.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron detalles de recepción"))
                    .stream()
                    .map(recepcion -> new DetalleRecepcionDTO(
                            recepcion.getIdDetalleRecepcion(),
                            recepcion.getOrdenRecepcion().getIdOrdenRecepcion(),
                            recepcion.getProducto(),
                            recepcion.getCantidad()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener detalles de la recepción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDetallePorId(@RequestParam Long id) {
        try {
            Optional<DetalleRecepcion> detalle = detalleRecepcionService.buscarPorId(id);
            if (detalle.isPresent()) {
                DetalleRecepcion det = detalle.get();
                DetalleRecepcionDTO dto = new DetalleRecepcionDTO(det.getIdDetalleRecepcion(),
                        det.getOrdenRecepcion().getIdOrdenRecepcion(), det.getProducto(), det.getCantidad());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Detalle no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearDetalleRecepcion(@RequestBody DetalleRecepcionDTO dto) {
        try {
            Optional<ProductoDTO> productoDTOOpt = productoController.buscarPorCodigoSKU(dto.getCodigoSku());
            ProductoDTO productoDTO;
            if (productoDTOOpt.isPresent()) {
                productoDTO = productoDTOOpt.get();
            } else {
                return new ResponseEntity<>("Producto no encontrado para el código SKU: " + dto.getCodigoSku(), HttpStatus.BAD_REQUEST);
            }

            DetalleRecepcion detalleRecepcion = new DetalleRecepcion();
            detalleRecepcion.setProducto(productoController.toEntity(productoDTO));
            detalleRecepcion.setCantidad(dto.getCantidad());

            detalleRecepcionService.crear(detalleRecepcion);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarDetalleRecepcion(@RequestBody DetalleRecepcionDTO detalleDTO) {
        try {
            // Fetch OrdenRecepcion using service instead of controller
            Optional<OrdenRecepcion> ordenOpt = ordenRecepcionService.buscarPorId(detalleDTO.getOrdenRecepcion());
            if (ordenOpt.isEmpty()) {
                return new ResponseEntity<>("La orden del detalle no existe, revise los datos", HttpStatus.NOT_FOUND);
            }

            Optional<ProductoDTO> productoDTOOpt = productoController.buscarPorCodigoSKU(detalleDTO.getCodigoSku());
            if (productoDTOOpt.isEmpty()) {
                return new ResponseEntity<>("Producto no encontrado para el código SKU: " + detalleDTO.getCodigoSku(), HttpStatus.BAD_REQUEST);
            }

            DetalleRecepcion detalle = new DetalleRecepcion();
            detalle.setOrdenRecepcion(ordenOpt.get());
            detalle.setProducto(productoController.toEntity(productoDTOOpt.get()));
            detalle.setCantidad(detalleDTO.getCantidad());

            detalleRecepcionService.actualizar(detalle);
            return new ResponseEntity<>(detalleDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarOrdenRecepcion(@RequestParam Long id) {
        try {
            detalleRecepcionService.eliminar(id);
            return new ResponseEntity<>("Detalle eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ordenId")
    public ResponseEntity<?> buscarDetallesPorOrdenRecepcionId(@RequestParam Long id) {
        try {
            Optional<List<DetalleRecepcion>> detalles = detalleRecepcionService.buscarDetallesPorOrden(id);
            if (detalles.isPresent()) {
                List<DetalleRecepcionDTO> detallesDTO = detalles.get().stream()
                        .map(det -> new DetalleRecepcionDTO(
                                det.getIdDetalleRecepcion(),
                                det.getOrdenRecepcion().getIdOrdenRecepcion(),
                                det.getProducto(),
                                det.getCantidad()))
                        .collect(Collectors.toList());
                return new ResponseEntity<>(detallesDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>("No se encontraron detalles para la orden", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public DetalleRecepcionDTO toDetalleRecepcionDTO(DetalleRecepcion detalle) {
        DetalleRecepcionDTO dto = new DetalleRecepcionDTO();
        dto.setCantidad(detalle.getCantidad());

        Producto producto = detalle.getProducto();
        if (producto != null) {
            dto.setIdProducto(producto.getId_producto());
            dto.setNombreProducto(producto.getNombre());
            dto.setUnidadMedida(producto.getUnidad_medida());
            dto.setCodigoSku(String.valueOf(producto.getCodigoSku()));
        }
        return dto;
    }

    public DetalleRecepcion toDetalleRecepcionEntity(DetalleRecepcionDTO dto) {
        DetalleRecepcion detalle = new DetalleRecepcion();
        detalle.setCantidad(dto.getCantidad());
        try {
            ResponseEntity<?> response = productoController.buscar(dto.getIdProducto());
            ProductoDTO prodDto = (ProductoDTO) response.getBody();
            if (prodDto != null) {
                detalle.setProducto(productoController.toEntity(prodDto));
            } else {
                prodDto = (ProductoDTO) productoController.crearConRetorno(new ProductoDTO(
                        dto.getNombreProducto(),
                        dto.getDescripcionProducto(),
                        dto.getUnidadMedida(),
                        dto.getCodigoSku(),
                        Calendar.getInstance().getTime())).getBody();
                detalle.setProducto(productoController.toEntity(prodDto));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir DTO a entidad: " + e.getMessage());
        }
        return detalle;
    }
}