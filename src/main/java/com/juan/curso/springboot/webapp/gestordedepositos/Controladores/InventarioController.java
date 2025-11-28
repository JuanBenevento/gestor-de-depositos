package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.InventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProveedorDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.InventarioServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UbicacionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@RestController
@RequestMapping("GestorDeDepositos/inventario")
public class InventarioController {

    private final ProductoServiceImpl productoService;
    private final UbicacionServiceImpl ubicacionService;
    private final InventarioServiceImpl inventarioService;

    public InventarioController(ProductoServiceImpl productoService,
    UbicacionServiceImpl ubicacionService,
    InventarioServiceImpl inventarioService){
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/stockTotalPorIdProducto")
    @Operation(summary = "Calcula el stock total de un producto por su ID, sumando las cantidades de todos los inventarios asociados.")
    public ResponseEntity<Integer> getStockTotalPorId(@RequestParam String id) throws RecursoNoEncontradoException {
        int stockTotal = inventarioService.calcularStockTotalPorIdProducto(parseLong(id));
        return ResponseEntity.ok(stockTotal);
    }

    @GetMapping("/stockTotalPorCodigoSku")
    @Operation(summary = "Calcula el stock total de un producto por su Codigo SKU, sumando las cantidades de todos los inventarios asociados.")
    public ResponseEntity<Integer> getStockTotalPorCodigoSku(@RequestParam String codigoSku) throws RecursoNoEncontradoException {
        int stockTotal = inventarioService.calcularStockTotalPorCodigoSku(codigoSku);
        return ResponseEntity.ok(stockTotal);
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los inventarios")
    public ResponseEntity<?> buscarTodos() {
        List<Inventario> inventarios = inventarioService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los inventarios"));

        List<InventarioDTO> dtoList = inventarios.stream()
                .map(InventarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Busca un inventario por su ID")
    public ResponseEntity<InventarioDTO> buscarPorId(@RequestParam Long id) {
        try {
            Inventario inventario = inventarioService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

            return ResponseEntity.ok(new InventarioDTO(inventario));

        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscarPorCodigoSkuProducto")
    @Operation(summary = "Este metodo busca un inventario por codigo sku producto")
    public ResponseEntity<?> buscarPorCodigoSku(@RequestParam String codigoSku) {
        try {
            List<Inventario> inventarios = inventarioService.buscarPorCodigoSku(codigoSku);
            if (inventarios.isEmpty()) {
                throw new RecursoNoEncontradoException("No se encontraron invetarios relacionados con ese codigo sku.");
            }

            List<InventarioDTO> dtoList = inventarios.stream()
                    .map(InventarioDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar inventarios", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un inventario")
    public ResponseEntity<InventarioDTO> crear(@RequestBody InventarioDTO inventarioDTO) {
        try {
            Inventario inventario = new Inventario();

            // Buscar ubicación
            Ubicacion ubicacion = ubicacionService.buscarPorId(inventarioDTO.getUbicacion().getIdUbicacion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));

            // Validar capacidad real
            int espacioDisponible = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
            if (inventarioDTO.getCantidad() > espacioDisponible) {
                throw new CapacidadExcedida("La ubicación no tiene capacidad suficiente");
            }

            inventario.setUbicacion(ubicacion);

            // Buscar producto
            Producto producto = productoService.buscarPorId(inventarioDTO.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            inventario.setProducto(producto);
            inventario.setCantidad(inventarioDTO.getCantidad());
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());

            // Crear
            inventario = inventarioService.crear(inventario);

            // ACTUALIZAR OCUPADO
            ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + inventario.getCantidad());
            ubicacionService.actualizar(ubicacion);

            return new ResponseEntity<>(new InventarioDTO(inventario), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear inventario: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo busca un inventario por id y lo actualiza")
    public ResponseEntity<InventarioDTO> actualizar(@RequestParam Long id,
                                                    @RequestBody InventarioDTO inventarioDTO) {
        try {
            Inventario inventarioExistente = inventarioService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

            Ubicacion ubicacionAnterior = inventarioExistente.getUbicacion();
            int cantidadAnterior = inventarioExistente.getCantidad();

            // Buscar nueva ubicación
            Ubicacion ubicacionNueva = ubicacionService.buscarPorId(inventarioDTO.getUbicacion().getIdUbicacion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación nueva no encontrada"));

            // Buscar producto
            Producto producto = productoService.buscarPorId(inventarioDTO.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            int cantidadNueva = inventarioDTO.getCantidad();

            // Si cambia de ubicación
            if (!ubicacionAnterior.getIdUbicacion().equals(ubicacionNueva.getIdUbicacion())) {

                // Restar de ubicación anterior
                int ocupadoAnterior = ubicacionAnterior.getOcupadoActual() - cantidadAnterior;
                if (ocupadoAnterior < 0) ocupadoAnterior = 0;
                ubicacionAnterior.setOcupadoActual(ocupadoAnterior);
                ubicacionService.actualizar(ubicacionAnterior);

                // Validar capacidad en la nueva
                int espacioDisponible = ubicacionNueva.getCapacidadMaxima() - ubicacionNueva.getOcupadoActual();
                if (cantidadNueva > espacioDisponible) {
                    throw new CapacidadExcedida("La nueva ubicación no tiene capacidad suficiente");
                }

                // Sumar en la nueva
                ubicacionNueva.setOcupadoActual(ubicacionNueva.getOcupadoActual() + cantidadNueva);
                ubicacionService.actualizar(ubicacionNueva);

            } else {
                // SI ES LA MISMA UBICACIÓN

                int diferencia = cantidadNueva - cantidadAnterior;

                // Si aumenta cantidad → verificar capacidad
                if (diferencia > 0) {
                    int espacioDisponible = ubicacionNueva.getCapacidadMaxima() - ubicacionNueva.getOcupadoActual();
                    if (diferencia > espacioDisponible) {
                        throw new CapacidadExcedida("La ubicación no tiene capacidad suficiente para aumentar esta cantidad");
                    }
                }

                // Actualizar ocupadoActual
                ubicacionNueva.setOcupadoActual(ubicacionNueva.getOcupadoActual() + diferencia);

                // Nunca negativo
                if (ubicacionNueva.getOcupadoActual() < 0) {
                    ubicacionNueva.setOcupadoActual(0);
                }

                ubicacionService.actualizar(ubicacionNueva);
            }

            // Actualizar INV
            inventarioExistente.setCantidad(cantidadNueva);
            inventarioExistente.setUbicacion(ubicacionNueva);
            inventarioExistente.setProducto(producto);
            inventarioExistente.setFecha_actualizacion(Calendar.getInstance().getTime());

            Inventario actualizado = inventarioService.actualizar(inventarioExistente);

            return ResponseEntity.ok(new InventarioDTO(actualizado));

        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar inventario: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un inventario por su id")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            Inventario inventario = inventarioService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

            Ubicacion ubicacion = inventario.getUbicacion();

            // Restar al ocupadoActual
            int nuevoOcupado = ubicacion.getOcupadoActual() - inventario.getCantidad();
            if (nuevoOcupado < 0) nuevoOcupado = 0;

            ubicacion.setOcupadoActual(nuevoOcupado);
            ubicacionService.actualizar(ubicacion);

            inventarioService.eliminar(id);

            return ResponseEntity.ok("Inventario eliminado con éxito");
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
