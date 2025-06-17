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

    @GetMapping("/buscarTodos")
    @Operation(summary = "Este metodo busca todos los inventarios")
    public ResponseEntity<?> buscarTodos() {
        List<Inventario> inventarios = inventarioService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron los inventarios"));

        List<InventarioDTO> dtoList = inventarios.stream()
                .map(InventarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
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
        try{
            Inventario inventario = new Inventario();
            Optional<Ubicacion> ubicacion = ubicacionService.buscarPorId(inventarioDTO.getUbicacion().getIdUbicacion());
            if(ubicacion.isPresent()){
                if(ubicacion.get().getCapacidadMaxima() > inventarioDTO.getCantidad()){
                    if(ubicacion.get().getCapacidadMaxima() >= inventarioDTO.getCantidad()){
                        inventario.setUbicacion(ubicacion.get());
                    }else{
                        throw new CapacidadExcedida("La ubicacion tiene espacio parcial, elija otra");
                    }
                }else{
                    throw new CapacidadExcedida("La ubicacion no tiene capacidad para la cantidad ingresada");
                }
            }else{
                throw new RecursoNoEncontradoException("Ubicacion no encontrada");
            }
            Optional<Producto> producto = productoService.buscarPorId(inventarioDTO.getProducto().getIdProducto());
            if(producto.isPresent()){
                inventario.setProducto(producto.get());
            }
            inventario.setCantidad(inventarioDTO.getCantidad());
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
            inventario = inventarioService.crear(inventario);
            return new ResponseEntity<>(new InventarioDTO(inventario), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("eliminarInventario")
    @Operation(summary = "Este metodo elimina un inventario por su id")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            inventarioService.eliminar(id);
            return ResponseEntity.ok("Inventario eliminado con éxito");
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar inventario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
