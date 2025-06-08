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
            Optional<Producto> productoElegido = productoServiceImpl.buscarPorId(dto.getProducto().getIdProducto());
            Optional<Ubicacion> ubicacion_origen = ubicacionServiceImpl.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion());
            Optional<Ubicacion> ubicacion_destino = ubicacionServiceImpl.buscarPorId(dto.getUbicacionDestino().getIdUbicacion());

            if(ubicacion_origen.isPresent() && ubicacion_destino.isPresent() && productoElegido.isPresent()) {
                Ubicacion origen = ubicacion_origen.get();
                Ubicacion destino = ubicacion_destino.get();
                Producto producto = productoElegido.get();

                MovimientoInventario movInventario = new MovimientoInventario(dto.getId_movimiento(), producto, origen, destino, dto.getCantidad(), dto.getEstado(), new Date());

                movInventario = movimientoInventarioServiceImpl.crearConRetorno(movInventario);
                dto = new MovimientoInventarioDTO(movInventario);
                return new ResponseEntity<>(dto, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear movimiento de inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca un inventario por su id")
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
            Optional<MovimientoInventario> movInventarioSelected = movimientoInventarioServiceImpl.buscarPorId(id);
            Optional<Ubicacion> ubicacion_origen = ubicacionServiceImpl.buscarPorId(dto.getUbicacionOrigen().getIdUbicacion());
            Optional<Ubicacion> ubicacion_destino = ubicacionServiceImpl.buscarPorId(dto.getUbicacionDestino().getIdUbicacion());
            Optional<Producto> reqProducto = productoServiceImpl.buscarPorId(dto.getProducto().getIdProducto());

            if(movInventarioSelected.isPresent() && ubicacion_origen.isPresent() && ubicacion_destino.isPresent() && reqProducto.isPresent()) {
                MovimientoInventario movInventario = movInventarioSelected.get();
                Ubicacion origen = ubicacion_origen.get();
                Ubicacion destino = ubicacion_destino.get();
                Producto producto = reqProducto.get();

                movInventario.setProducto(producto);
                movInventario.setUbicacionOrigen(origen);
                movInventario.setUbicacionDestino(destino);
                movInventario.setCantidad(dto.getCantidad());
                movInventario.setEstado(dto.getEstado());
                movInventario.setFecha(new Date());

                movInventario = movimientoInventarioServiceImpl.actualizar(movInventario);
                dto = new MovimientoInventarioDTO(movInventario);
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Movimiento de inventario no encontrado", HttpStatus.NOT_FOUND);
            }
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
}
