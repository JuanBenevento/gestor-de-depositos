package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.MovimientoInventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UbicacionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/movimientoInventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioServiceImpl movimientoService;
    private final UbicacionServiceImpl ubicacionService;
    private final ProductoServiceImpl productoService;

    @Autowired
    public MovimientoInventarioController(
            MovimientoInventarioServiceImpl movimientoService,
            UbicacionServiceImpl ubicacionService,
            ProductoServiceImpl productoService
    ) {
        this.movimientoService = movimientoService;
        this.ubicacionService = ubicacionService;
        this.productoService = productoService;
    }

    @GetMapping("/todos")
    @Operation(summary = "Lista todos los movimientos de inventario")
    public ResponseEntity<?> buscarTodos() {
        List<MovimientoInventarioDTO> movimientos = movimientoService.buscarTodos()
                .orElse(List.of())
                .stream()
                .map(MovimientoInventarioDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(movimientos, HttpStatus.OK);
    }

    @PostMapping("/crearMovimiento")
    @Operation(summary = "Crea un nuevo movimiento de inventario")
    public ResponseEntity<?> crear(@RequestBody MovimientoInventarioDTO dto) {

        try {
            if (dto == null || dto.getProducto() == null ||
                    dto.getUbicacionOrigen() == null || dto.getUbicacionDestino() == null ||
                    dto.getCantidad() <= 0) {

                return new ResponseEntity<>("Datos incompletos.", HttpStatus.BAD_REQUEST);
            }

            // Validación fecha futura
            if (dto.getFecha() != null && dto.getFecha().after(new Date())) {
                return new ResponseEntity<>("La fecha no puede ser futura.", HttpStatus.BAD_REQUEST);
            }

            Optional<Producto> productoOpt = productoService.buscarPorId(dto.getProducto().getIdProducto());
            Optional<Ubicacion> origenOpt = ubicacionService.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion());
            Optional<Ubicacion> destinoOpt = ubicacionService.buscarPorId(dto.getUbicacionDestino().getIdUbicacion());

            if (productoOpt.isEmpty() || origenOpt.isEmpty() || destinoOpt.isEmpty()) {
                return new ResponseEntity<>("Producto o ubicaciones no encontrados.", HttpStatus.NOT_FOUND);
            }

            Producto producto = productoOpt.get();
            Ubicacion origen = origenOpt.get();
            Ubicacion destino = destinoOpt.get();

            // Validaciones de capacidad / stock
            if (origen.getOcupadoActual() < dto.getCantidad()) {
                return new ResponseEntity<>("Stock insuficiente en ubicación de origen.", HttpStatus.BAD_REQUEST);
            }
            if (destino.getCapacidadMaxima() - destino.getOcupadoActual() < dto.getCantidad()) {
                return new ResponseEntity<>("Capacidad insuficiente en destino.", HttpStatus.BAD_REQUEST);
            }

            // Actualizar stock origen/destino
            origen.setOcupadoActual(origen.getOcupadoActual() - dto.getCantidad());
            destino.setOcupadoActual(destino.getOcupadoActual() + dto.getCantidad());

            ubicacionService.actualizar(origen);
            ubicacionService.actualizar(destino);

            // Crear movimiento SIN ID → Hibernate lo genera
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setUbicacionOrigen(origen);
            movimiento.setUbicacionDestino(destino);
            movimiento.setCantidad(dto.getCantidad());
            movimiento.setEstado(dto.getEstado());
            movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : new Date());

            MovimientoInventario guardado = movimientoService.crear(movimiento);

            return new ResponseEntity<>(new MovimientoInventarioDTO(guardado), HttpStatus.CREATED);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear el movimiento", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca un movimiento por ID")
    public ResponseEntity<?> buscar(@RequestParam Long id) {

        Optional<MovimientoInventario> movOpt = movimientoService.buscarPorId(id);

        if (movOpt.isEmpty()) {
            return new ResponseEntity<>("Movimiento no encontrado", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MovimientoInventarioDTO(movOpt.get()), HttpStatus.OK);
    }

    @PutMapping("/editar")
    @Operation(summary = "Edita un movimiento de inventario")
    public ResponseEntity<?> editar(@RequestParam Long id, @RequestBody MovimientoInventarioDTO dto) {

        Optional<MovimientoInventario> movOpt = movimientoService.buscarPorId(id);

        if (movOpt.isEmpty()) {
            return new ResponseEntity<>("Movimiento no encontrado", HttpStatus.NOT_FOUND);
        }

        MovimientoInventario movimiento = movOpt.get();

        Optional<Producto> productoOpt = productoService.buscarPorId(dto.getProducto().getIdProducto());
        Optional<Ubicacion> origenOpt = ubicacionService.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion());
        Optional<Ubicacion> destinoOpt = ubicacionService.buscarPorId(dto.getUbicacionDestino().getIdUbicacion());

        if (productoOpt.isEmpty() || origenOpt.isEmpty() || destinoOpt.isEmpty()) {
            return new ResponseEntity<>("Producto o ubicaciones no encontrados", HttpStatus.NOT_FOUND);
        }

        movimiento.setProducto(productoOpt.get());
        movimiento.setUbicacionOrigen(origenOpt.get());
        movimiento.setUbicacionDestino(destinoOpt.get());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setEstado(dto.getEstado());
        movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : movimiento.getFecha());

        MovimientoInventario actualizado = movimientoService.actualizar(movimiento);

        return new ResponseEntity<>(new MovimientoInventarioDTO(actualizado), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Elimina un movimiento de inventario")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {

        movimientoService.eliminar(id);
        return new ResponseEntity<>("Movimiento eliminado correctamente", HttpStatus.OK);
    }
}
