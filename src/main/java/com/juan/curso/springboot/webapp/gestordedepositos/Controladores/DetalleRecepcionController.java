package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleRecepcionServiceImpl;
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
    @Autowired
    public DetalleRecepcionController(DetalleRecepcionServiceImpl detalleRecepcionService) {
        this.detalleRecepcionService = detalleRecepcionService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<DetalleRecepcionDTO> productos = detalleRecepcionService.buscarTodos()
                    .orElseThrow()
                    .stream()
                    .map(recepcion -> new DetalleRecepcionDTO(
                            recepcion.getIdDetalleRecepcion(),
                            recepcion.getOrdenRecepcion(),
                            recepcion.getProducto(),
                            recepcion.getCantidad()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener detalles de la recepcion", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDetallePorId(@RequestParam Long id) {
        try {
            Optional<DetalleRecepcion> detalle = detalleRecepcionService.buscarPorId(id);
            if (detalle.isPresent()) {
                DetalleRecepcion det = detalle.get();
                DetalleRecepcionDTO dto = new DetalleRecepcionDTO(det.getIdDetalleRecepcion(),
                        det.getOrdenRecepcion(), det.getProducto(), det.getCantidad());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Detalle no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar detalle", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearDetalleRecepcion(@RequestBody DetalleRecepcionDTO dto) {
        try {
            DetalleRecepcion producto = new DetalleRecepcion(dto.getOrdenRecepcion(),dto.getProducto(),dto.getCantidad());
            detalleRecepcionService.crear(producto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarDetalleRecepcion(@RequestBody DetalleRecepcionDTO detalleDTO) {
        try {
            DetalleRecepcion detalle = new DetalleRecepcion(detalleDTO.getOrdenRecepcion(),detalleDTO.getProducto()
                    ,detalleDTO.getCantidad());
            detalleRecepcionService.actualizar(detalle);
            return new ResponseEntity<>(detalleDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar Detalle", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarOrdenRecepcion(@RequestParam Long id) {
        try {
            detalleRecepcionService.eliminar(id);
            return new ResponseEntity<>("Orden eliminada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ordenId")
    public ResponseEntity<?> buscarDetallesPorOrdenRecepcionId(@RequestParam Long id) {
        List<DetalleRecepcionDTO> detallesdto= null;
        try{
            Optional<List<DetalleRecepcion>> detalles = detalleRecepcionService.buscarDetallesPorOrden(id);
            if(detalles.isPresent()){
                detallesdto = detalles.get().stream().map(
                        det -> new DetalleRecepcionDTO(det.getIdDetalleRecepcion(),
                                det.getOrdenRecepcion(),det.getProducto(),det.getCantidad())
                ).collect(Collectors.toList());
            }
        }catch(Exception e){
            return new ResponseEntity<>("Error al buscar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(detallesdto, HttpStatus.OK);

    }
}
