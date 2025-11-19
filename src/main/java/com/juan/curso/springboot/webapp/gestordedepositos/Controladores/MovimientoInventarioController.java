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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/movimientoInventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioServiceImpl movimientoInventarioServiceImpl;
    private final UbicacionServiceImpl ubicacionServiceImpl;
    private final ProductoServiceImpl productoServiceImpl;
    @Autowired
    public MovimientoInventarioController( MovimientoInventarioServiceImpl movimientoInventarioServiceImpl,
    UbicacionServiceImpl ubicacionServiceImpl,
    ProductoServiceImpl productoServiceImpl){
        this.movimientoInventarioServiceImpl = movimientoInventarioServiceImpl;
        this.ubicacionServiceImpl = ubicacionServiceImpl;
        this.productoServiceImpl = productoServiceImpl;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los movimientos del inventario")
    public ResponseEntity<?> buscarTodos() {
        List<MovimientoInventarioDTO> movimientosInventario = movimientoInventarioServiceImpl.buscarTodos().orElseThrow().stream().
                map(MovimientoInventarioDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(movimientosInventario, HttpStatus.OK);
    }

    @PostMapping("/crearMovimiento")
    @Operation(summary = "Este metodo crea un nuevo movimiento de inventario")
    public ResponseEntity<?> crear(@RequestBody MovimientoInventarioDTO dto) {
        try {
            if (dto == null || dto.getProducto() == null || dto.getUbicacionOrigen() == null
                    || dto.getUbicacionDestino() == null || dto.getCantidad() <= 0) {
                return new ResponseEntity<>("Payload incompleto o cantidad inválida", HttpStatus.BAD_REQUEST);
            }

            if (dto.getFecha() != null && dto.getFecha().after(new Date())) {
                return new ResponseEntity<>("La fecha del movimiento no puede ser futura", HttpStatus.BAD_REQUEST);
            }

            Optional<Producto> productoElegido = productoServiceImpl.buscarPorId(dto.getProducto().getIdProducto());
            Optional<Ubicacion> ubicacionOrigenOpt = ubicacionServiceImpl.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion());
            Optional<Ubicacion> ubicacionDestinoOpt = ubicacionServiceImpl.buscarPorId(dto.getUbicacionDestino().getIdUbicacion());

            if (productoElegido.isEmpty() || ubicacionOrigenOpt.isEmpty() || ubicacionDestinoOpt.isEmpty()) {
                return new ResponseEntity<>("Producto o ubicaciones no encontrados", HttpStatus.NOT_FOUND);
            }

            Ubicacion origen = ubicacionOrigenOpt.get();
            Ubicacion destino = ubicacionDestinoOpt.get();
            int cantidad = dto.getCantidad();

            if (origen.getOcupadoActual() < cantidad) {
                return new ResponseEntity<>("La ubicación de origen no tiene stock suficiente", HttpStatus.BAD_REQUEST);
            }
            if (destino.getCapacidadMaxima() - destino.getOcupadoActual() < cantidad) {
                return new ResponseEntity<>("La ubicación de destino no tiene capacidad disponible", HttpStatus.BAD_REQUEST);
            }

            int stockInicialOrigen = origen.getOcupadoActual();
            int stockInicialDestino = destino.getOcupadoActual();

            try {
                origen.setOcupadoActual(stockInicialOrigen - cantidad);
                destino.setOcupadoActual(stockInicialDestino + cantidad);
                ubicacionServiceImpl.actualizar(origen);
                ubicacionServiceImpl.actualizar(destino);
            } catch (Exception ex) {
                origen.setOcupadoActual(stockInicialOrigen);
                destino.setOcupadoActual(stockInicialDestino);
                throw ex;
            }

            Date fechaMovimiento = dto.getFecha() != null ? dto.getFecha() : new Date();
            MovimientoInventario movInventario = new MovimientoInventario(dto.getId_movimiento(), productoElegido.get(),
                    origen, destino, cantidad, dto.getEstado(), fechaMovimiento);

            MovimientoInventario creado = movimientoInventarioServiceImpl.crear(movInventario);
            return new ResponseEntity<>(new MovimientoInventarioDTO(creado), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca un movimiento inventario por su id")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<MovimientoInventario> movInventarioSelected = movimientoInventarioServiceImpl.buscarPorId(id);
            if (movInventarioSelected.isPresent()) {
                MovimientoInventario movimInvent = movInventarioSelected.get();
                return new ResponseEntity<>(new MovimientoInventarioDTO(movimInvent), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Movimiento de inventario no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/editar")
    @Operation(summary = "Este metodo edita un movimiento")
    public ResponseEntity<?> editar(@RequestParam Long id, @RequestBody MovimientoInventarioDTO dto) {
        try {
            if (dto == null || dto.getProducto() == null || dto.getUbicacionOrigen() == null
                    || dto.getUbicacionDestino() == null || dto.getCantidad() <= 0) {
                return new ResponseEntity<>("Payload incompleto o cantidad inválida", HttpStatus.BAD_REQUEST);
            }

            if (dto.getFecha() != null && dto.getFecha().after(new Date())) {
                return new ResponseEntity<>("La fecha del movimiento no puede ser futura", HttpStatus.BAD_REQUEST);
            }

            Optional<MovimientoInventario> movOpt = movimientoInventarioServiceImpl.buscarPorId(id);
            if (movOpt.isEmpty()) {
                return new ResponseEntity<>("Movimiento de inventario no encontrado", HttpStatus.NOT_FOUND);
            }

            MovimientoInventario movimiento = movOpt.get();
            Ubicacion origenNuevo = ubicacionServiceImpl.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion()).orElse(null);
            Ubicacion destinoNuevo = ubicacionServiceImpl.buscarPorId(dto.getUbicacionDestino().getIdUbicacion()).orElse(null);
            Producto producto = productoServiceImpl.buscarPorId(dto.getProducto().getIdProducto()).orElse(null);

            if (origenNuevo == null || destinoNuevo == null || producto == null) {
                return new ResponseEntity<>("Producto o ubicaciones no encontrados", HttpStatus.NOT_FOUND);
            }

            Ubicacion origenAnterior = ubicacionServiceImpl.buscarPorId(movimiento.getUbicacionOrigen().getIdUbicacion())
                    .orElse(origenNuevo);
            Ubicacion destinoAnterior = ubicacionServiceImpl.buscarPorId(movimiento.getUbicacionDestino().getIdUbicacion())
                    .orElse(destinoNuevo);

            if (origenNuevo.getIdUbicacion().equals(origenAnterior.getIdUbicacion())) {
                origenNuevo = origenAnterior;
            }
            if (destinoNuevo.getIdUbicacion().equals(destinoAnterior.getIdUbicacion())) {
                destinoNuevo = destinoAnterior;
            }

            Map<Ubicacion, Integer> ocupacionesOriginales = new HashMap<>();
            Set<Ubicacion> ubicacionesAActualizar = new HashSet<>();
            registrarUbicacion(origenAnterior, ocupacionesOriginales, ubicacionesAActualizar);
            registrarUbicacion(destinoAnterior, ocupacionesOriginales, ubicacionesAActualizar);
            registrarUbicacion(origenNuevo, ocupacionesOriginales, ubicacionesAActualizar);
            registrarUbicacion(destinoNuevo, ocupacionesOriginales, ubicacionesAActualizar);

            int cantidadAnterior = movimiento.getCantidad();
            int cantidadNueva = dto.getCantidad();

            try {
                destinoAnterior.setOcupadoActual(destinoAnterior.getOcupadoActual() - cantidadAnterior);
                if (destinoAnterior.getOcupadoActual() < 0) {
                    throw new IllegalArgumentException("La ubicación destino original quedaría con stock negativo");
                }
                origenAnterior.setOcupadoActual(origenAnterior.getOcupadoActual() + cantidadAnterior);

                if (origenNuevo.getOcupadoActual() < cantidadNueva) {
                    throw new IllegalArgumentException("La ubicación de origen no tiene stock suficiente");
                }
                if (destinoNuevo.getCapacidadMaxima() - destinoNuevo.getOcupadoActual() < cantidadNueva) {
                    throw new IllegalArgumentException("La ubicación de destino no tiene capacidad disponible");
                }

                origenNuevo.setOcupadoActual(origenNuevo.getOcupadoActual() - cantidadNueva);
                destinoNuevo.setOcupadoActual(destinoNuevo.getOcupadoActual() + cantidadNueva);

                for (Ubicacion ubicacion : ubicacionesAActualizar) {
                    ubicacionServiceImpl.actualizar(ubicacion);
                }
            } catch (IllegalArgumentException e) {
                restaurarOcupaciones(ocupacionesOriginales);
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                restaurarOcupaciones(ocupacionesOriginales);
                throw e;
            }

            movimiento.setProducto(producto);
            movimiento.setUbicacionOrigen(origenNuevo);
            movimiento.setUbicacionDestino(destinoNuevo);
            movimiento.setCantidad(cantidadNueva);
            movimiento.setEstado(dto.getEstado());
            movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : movimiento.getFecha());

            MovimientoInventario actualizado = movimientoInventarioServiceImpl.actualizar(movimiento);
            return new ResponseEntity<>(new MovimientoInventarioDTO(actualizado), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al intentar actualizar movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping ("/eliminar")
    @Operation(summary = "Este metodo elimina un movimiento de inventario")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            movimientoInventarioServiceImpl.eliminar(id);
            return new ResponseEntity<>("Movimiento de inventario eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al intentar eliminar el movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    private void registrarUbicacion(Ubicacion ubicacion, Map<Ubicacion, Integer> ocupaciones, Set<Ubicacion> ubicaciones) {
        if (ubicacion != null) {
            ocupaciones.putIfAbsent(ubicacion, ubicacion.getOcupadoActual());
            ubicaciones.add(ubicacion);
        }
    }

    private void restaurarOcupaciones(Map<Ubicacion, Integer> ocupaciones) {
        ocupaciones.forEach(Ubicacion::setOcupadoActual);
    }
}
