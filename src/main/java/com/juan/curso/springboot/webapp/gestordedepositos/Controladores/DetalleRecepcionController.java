package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/detalleRecepcion")
public class DetalleRecepcionController {

    private final DetalleRecepcionServiceImpl detalleRecepcionService;
    private final OrdenRecepcionServiceImpl ordenRecepcionService;
    private final ProductoServiceImpl productoService;

    @Autowired
    public DetalleRecepcionController(DetalleRecepcionServiceImpl detalleRecepcionService,
    OrdenRecepcionServiceImpl ordenRecepcionService,
    ProductoServiceImpl productoService) {
        this.detalleRecepcionService = detalleRecepcionService;
        this.ordenRecepcionService = ordenRecepcionService;
        this.productoService = productoService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<DetalleRecepcionDTO> detallesRecepcion = detalleRecepcionService.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron detalles de recepción"))
                    .stream()
                    .map(detalle -> new DetalleRecepcionDTO(detalle))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(detallesRecepcion, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener detalles de la recepción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscarDetallePorId")
    public ResponseEntity<?> buscarDetallePorIdDetalle(@RequestParam Long id) {
        try {
            Optional<DetalleRecepcion> detalle = detalleRecepcionService.buscarPorId(id);
            if (detalle.isPresent()) {
                DetalleRecepcion det = detalle.get();
                DetalleRecepcionDTO dto = new DetalleRecepcionDTO(det);
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Detalle no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearDetalleRecepcion")
    public ResponseEntity<?> crearDetalleRecepcion(@RequestBody DetalleRecepcionDTO dto) {

        try {
            DetalleRecepcion detalleRecepcion = new DetalleRecepcion();
            Optional<OrdenRecepcion> orden = ordenRecepcionService.buscarPorId(dto.getOrden().getIdOrdenRecepcion());
            if(orden.isPresent()) {
                detalleRecepcion.setOrdenRecepcion(orden.get());
            }else {
                return new ResponseEntity<>("Orden asociada al detalle no encontrada", HttpStatus.NOT_FOUND);
            }
            Producto producto = productoService.buscarPorCodigoSKU(dto.getProducto().getCodigoSku());
            if (producto != null) {
                detalleRecepcion.setProducto(producto);
            } else {
                return new ResponseEntity<>("Producto no encontrado para el código SKU: " + dto.getProducto().getCodigoSku(), HttpStatus.BAD_REQUEST);
            }
            detalleRecepcion.setCantidad(dto.getCantidad());

            detalleRecepcionService.crear(detalleRecepcion);

            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarDetalle")
    public ResponseEntity<?> actualizarDetalleRecepcion(@RequestBody DetalleRecepcionDTO detalleDTO) {
        try {
            DetalleRecepcion detalle = new DetalleRecepcion();
            Optional<DetalleRecepcion> detallesEncontrados = detalleRecepcionService.buscarPorId(detalleDTO.getIdDetalleRecepcion());
            if (detallesEncontrados.isPresent()) {
                detalle = detallesEncontrados.get();
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setProducto(productoService.buscarPorCodigoSKU(detalleDTO.getProducto().getCodigoSku()));
            }
            detalleRecepcionService.actualizar(detalle);
            return new ResponseEntity<>(detalleDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarDetalleConIdDet")
    public ResponseEntity<?> eliminarDetalle(@RequestParam Long idDet) {
        try {
            detalleRecepcionService.eliminar(idDet);
            return new ResponseEntity<>("Detalle eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarDetallesDeOrden")
    public ResponseEntity<?> eliminarDetallesDeOrden(@RequestParam Long idOrden) {
        try {
            Optional<List<DetalleRecepcion>> detalles = detalleRecepcionService.buscarDetallesPorOrden(idOrden);
            if(detalles.isPresent()){
                detalleRecepcionService.eliminarTodos(detalles.get());
                return new ResponseEntity<>("Detalle eliminado", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Detalles no encontrados", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscarDetallesPorIdOrden")
    public ResponseEntity<?> buscarDetallesPorOrdenRecepcionId(@RequestParam Long idOrden) {
        try {
            Optional<List<DetalleRecepcion>> detalles = detalleRecepcionService.buscarDetallesPorOrden(idOrden);
            if (detalles.isPresent()) {
                List<DetalleRecepcionDTO> detallesDTO = detalles.get().stream()
                        .map(det -> new DetalleRecepcionDTO(det))
                        .collect(Collectors.toList());
                return new ResponseEntity<>(detallesDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>("No se encontraron detalles para la orden", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}