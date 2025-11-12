package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionBulkRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionItemDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
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
    @Operation(summary = "Este metodo busca todos los detalles de recepcion que se encuentran en la base de datos")
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
    @Operation(summary = "Este metodo busca un detalle de recepcion por el id tipo LONG")
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
    @Operation(summary = "Este metodo crea un detalle de recepcion para una orden ya creada. Valida que exista la orden y que el producto tambien exista")
    public ResponseEntity<?> crearDetalleRecepcion(@RequestBody DetalleRecepcionDTO dto) {

        try {
            DetalleRecepcion detalleRecepcion = new DetalleRecepcion();
            Long idOrden = dto.getIdOrdenRecepcion();
            if (idOrden == null && dto.getOrden() != null) {
                idOrden = dto.getOrden().getIdOrdenRecepcion();
            }
            if (idOrden == null) {
                return new ResponseEntity<>("Debe indicar el identificador de la orden asociada", HttpStatus.BAD_REQUEST);
            }
            Optional<OrdenRecepcion> orden = ordenRecepcionService.buscarPorId(idOrden);
            if(orden.isPresent()) {
                detalleRecepcion.setOrdenRecepcion(orden.get());
            }else {
                return new ResponseEntity<>("Orden asociada al detalle no encontrada", HttpStatus.NOT_FOUND);
            }
            String codigoSku = dto.getCodigoSku();
            if (codigoSku == null && dto.getProducto() != null) {
                codigoSku = dto.getProducto().getCodigoSku();
            }
            if (codigoSku == null || codigoSku.isBlank()) {
                return new ResponseEntity<>("Debe indicar un código SKU válido", HttpStatus.BAD_REQUEST);
            }
            Producto producto = productoService.buscarPorCodigoSKU(codigoSku);
            if (producto != null) {
                detalleRecepcion.setProducto(producto);
            } else {
                return new ResponseEntity<>("Producto no encontrado para el código SKU: " + codigoSku, HttpStatus.BAD_REQUEST);
            }
            detalleRecepcion.setCantidad(dto.getCantidad());

            detalleRecepcion = detalleRecepcionService.crear(detalleRecepcion);
            return new ResponseEntity<>(new DetalleRecepcionDTO(detalleRecepcion), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearDetallesRecepcion")
    @Operation(summary = "Este metodo crea multiples detalles de recepcion para una orden existente")
    public ResponseEntity<?> crearDetallesRecepcion(@Valid @RequestBody DetalleRecepcionBulkRequest request) {
        try {
            Long idOrden = request.getIdOrdenRecepcion();
            if (idOrden == null) {
                idOrden = request.getDetalles().stream()
                        .map(DetalleRecepcionItemDTO::getIdOrdenRecepcion)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);
            }
            if (idOrden == null) {
                return new ResponseEntity<>("Debe indicar el identificador de la orden asociada", HttpStatus.BAD_REQUEST);
            }

            Optional<OrdenRecepcion> ordenOpt = ordenRecepcionService.buscarPorId(idOrden);
            if (ordenOpt.isEmpty()) {
                return new ResponseEntity<>("Orden asociada al detalle no encontrada", HttpStatus.NOT_FOUND);
            }
            OrdenRecepcion orden = ordenOpt.get();

            List<DetalleRecepcion> detallesParaGuardar = new ArrayList<>();
            for (DetalleRecepcionItemDTO item : request.getDetalles()) {
                if (item.getIdOrdenRecepcion() != null && !Objects.equals(item.getIdOrdenRecepcion(), idOrden)) {
                    return new ResponseEntity<>("Todos los detalles deben pertenecer a la misma orden", HttpStatus.BAD_REQUEST);
                }

                String codigoSku = item.getCodigoSku();
                if (codigoSku == null && item.getProducto() != null) {
                    codigoSku = item.getProducto().getCodigoSku();
                }
                if (codigoSku == null || codigoSku.isBlank()) {
                    return new ResponseEntity<>("Debe indicar un código SKU válido", HttpStatus.BAD_REQUEST);
                }

                Producto producto = productoService.buscarPorCodigoSKU(codigoSku);
                if (producto == null) {
                    return new ResponseEntity<>("Producto no encontrado para el código SKU: " + codigoSku, HttpStatus.BAD_REQUEST);
                }
                DetalleRecepcion detalle = new DetalleRecepcion();
                detalle.setOrdenRecepcion(orden);
                detalle.setProducto(producto);
                detalle.setCantidad(item.getCantidad());
                detallesParaGuardar.add(detalle);
            }

            List<DetalleRecepcionDTO> respuesta = detalleRecepcionService.crearTodos(detallesParaGuardar)
                    .stream()
                    .map(DetalleRecepcionDTO::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear detalles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarDetalle")
    @Operation(summary = "Este metodo actualiza un detalle de recepcion de una orden ya creada")
    public ResponseEntity<?> actualizarDetalleRecepcion(@RequestBody DetalleRecepcionDTO detalleDTO) {
        try {
            DetalleRecepcion detalle = new DetalleRecepcion();
            Optional<DetalleRecepcion> detallesEncontrados = detalleRecepcionService.buscarPorId(detalleDTO.getIdDetalleRecepcion());
            if (detallesEncontrados.isPresent()) {
                detalle = detallesEncontrados.get();
                detalle.setCantidad(detalleDTO.getCantidad());
                String codigoSku = detalleDTO.getCodigoSku();
                if (codigoSku == null && detalleDTO.getProducto() != null) {
                    codigoSku = detalleDTO.getProducto().getCodigoSku();
                }
                if (codigoSku != null && !codigoSku.isBlank()) {
                    detalle.setProducto(productoService.buscarPorCodigoSKU(codigoSku));
                }
            }
            detalleRecepcionService.actualizar(detalle);
            return new ResponseEntity<>(detalleDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarDetalleConIdDet")
    @Operation(summary = "Este metodo elimina un detalle de una orden ya creada-")
    public ResponseEntity<?> eliminarDetalle(@RequestParam Long idDet) {
        try {
            detalleRecepcionService.eliminar(idDet);
            return new ResponseEntity<>("Detalle eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar detalle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarDetallesDeOrden")
    @Operation(summary = "Este metodo elimina TODOS los detalles de una orden")
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
    @Operation(summary = "Este metodo busca todos los detalles por el id de orden tipo LONG")
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