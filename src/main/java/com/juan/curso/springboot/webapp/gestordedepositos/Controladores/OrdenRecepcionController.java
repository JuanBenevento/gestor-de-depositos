package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes")
public class OrdenRecepcionController {

    @Autowired
    OrdenRecepcionServiceImpl ordenRecepcionService;
    @Autowired
    ProveedorServiceImpl proveedorServiceImpl;
    @Autowired
    ProductoServiceImpl productoService;
    @Autowired
    DetalleRecepcionServiceImpl detalleRecepcionService;
    @Autowired
    InventarioServiceImpl inventarioService;

    public OrdenRecepcionController() {
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<OrdenRecepcion> ordenes = ordenRecepcionService.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron órdenes de recepción"));
            List<OrdenRecepcionDTO> ordenesDTO = ordenes.stream()
                    .map(orden -> new OrdenRecepcionDTO(orden))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(ordenesDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener órdenes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<OrdenRecepcion> orden = ordenRecepcionService.buscarPorId(id);
            if (orden.isPresent()) {
                OrdenRecepcionDTO dto = new OrdenRecepcionDTO(orden.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearOrdenRecepcion")
    public ResponseEntity<?> crearOrdenRecepcion(@RequestBody OrdenRecepcionDTO dto) {
        try {
            OrdenRecepcion orden = new OrdenRecepcion();

            // Buscar proveedor
            Proveedor proveedor = proveedorServiceImpl.buscarPorId(dto.getIdProveedor())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            orden.setProveedor(proveedor);
            orden.setFecha(Calendar.getInstance().getTime());
            orden.setEstado(dto.getEstado());

            // Procesar detalles
            List<DetalleRecepcion> detalles = new ArrayList<>();
            for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
                DetalleRecepcion detalle = new DetalleRecepcion();
                Optional<Producto> productoExistente = Optional.ofNullable(productoService.buscarPorCodigoSKU(detalleDTO.getProducto().getCodigoSku()));
                if (productoExistente.isPresent()) {
                    detalle.setProducto(productoExistente.get());
                } else {

                    Producto retorno =  productoService.crearConRetorno(detalleDTO.getProducto());
                    detalle.setProducto(retorno);
                }
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setOrdenRecepcion(orden);
                detalles.add(detalle);
                inventarioService.agregarMercaderia(detalle);
            }

            orden.setDetalles(detalles);
            OrdenRecepcion ordenCreada = ordenRecepcionService.crearConRetorno(orden);
            OrdenRecepcionDTO ordenDTO = new OrdenRecepcionDTO();
            if(ordenCreada!= null && ordenCreada.getDetalles() != null) {
                ordenDTO = new OrdenRecepcionDTO(ordenCreada);
                if (ordenDTO != null) {
                    return new ResponseEntity<>(ordenDTO, HttpStatus.CREATED);
                }
            }
            return new ResponseEntity<>(ordenDTO, HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarEstadoOrden")
    public ResponseEntity<?> actualizarEstadoOrden(@RequestParam Long idOrden, @RequestParam String estado) {
        try {
            Optional<OrdenRecepcion> existingOrden = ordenRecepcionService.buscarPorId(idOrden);
            if (existingOrden.isEmpty()) {
                return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
            }

            OrdenRecepcion orden = existingOrden.get();

            orden.setEstado(EstadosDeOrden.valueOf(estado));

            orden = ordenRecepcionService.actualizar(orden);

            return new ResponseEntity<>(new OrdenRecepcionDTO(orden), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarOrden")
    public ResponseEntity<?> eliminar(@RequestParam Long idOrden) {
        try {
            OrdenRecepcion orden = ordenRecepcionService.buscarPorId(idOrden)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            orden.getDetalles().clear();
            ordenRecepcionService.eliminar(orden.getIdOrdenRecepcion());
            return new ResponseEntity<>("Orden eliminada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}